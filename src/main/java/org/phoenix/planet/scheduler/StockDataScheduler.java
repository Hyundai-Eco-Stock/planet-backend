package org.phoenix.planet.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.DistributedScheduled;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.dto.eco_stock.raw.OhlcDto;
import org.phoenix.planet.dto.eco_stock.raw.RedisStockPriceHistory;
import org.phoenix.planet.dto.eco_stock.raw.StockMinutePriceHistory;
import org.phoenix.planet.dto.eco_stock.raw.VolumeDto;
import org.phoenix.planet.dto.eco_stock.response.ChartSingleDataResponse;
import org.phoenix.planet.repository.ChartDataRedisRepository;
import org.phoenix.planet.repository.ChartDataSecondRedisRepository;
import org.phoenix.planet.service.eco_stock.EcoStockMinutePriceHistoryService;
import org.phoenix.planet.service.eco_stock.EcoStockPriceHistoryService;
import org.phoenix.planet.service.eco_stock.EcoStockService;
import org.phoenix.planet.service.eco_stock.StockTradeProcessor;
import org.phoenix.planet.util.ecoStock.StockChartUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockDataScheduler {

    private final EcoStockPriceHistoryService ecoStockPriceHistoryService;
    private final EcoStockService ecoStockService;
    private final ChartDataRedisRepository chartDataRedisRepository;
    private final ChartDataSecondRedisRepository chartDataSecondRedisRepository;
    private final EcoStockMinutePriceHistoryService ecoStockMinutePriceHistoryService;
    private final EcoStockMinutePriceHistoryService ecoStockMinutePriceService;

    @Scheduled(cron = "0 * * * * *") // 1분마다
    @DistributedScheduled(lockKey = "ecoStock:price:update2:lock")
    public void broadcastStockData() {

        LocalDateTime time = LocalDateTime.now()
            .withSecond(0)   // 초를 0으로
            .withNano(0);

        LocalDateTime targetTime = time
            .minusMinutes(1)
            .withSecond(0)   // 초를 0으로
            .withNano(0);

        List<EcoStock> ecoStocks = ecoStockService.findAll();

        for (EcoStock stock : ecoStocks) {

            Long stockId = stock.id();

            if (!chartDataRedisRepository.existOhlcDto(stockId)
                || !chartDataRedisRepository.existVolumeDto(stockId)) {

                log.info("차트 초기화 필요");
                ecoStockPriceHistoryService.updateRedisData(stockId);
            }

            // 새 방법 (1번 Redis 호출)
            ChartSingleDataResponse singleData = chartDataSecondRedisRepository.getAndPushMinuteChartData(
                stockId, targetTime);

            if (singleData.ohlcData() != null) {
                insertStockMinutePriceHistory(stockId, targetTime, singleData.ohlcData(),
                    singleData.volumeData());
            }

            syncPriceHistoryToDb(stockId);
        }
    }

    private void syncPriceHistoryToDb(Long stockId) {

        List<String> historyJsonList = chartDataSecondRedisRepository.getPriceHistoryData(stockId);

        log.info("historyJsonList: {}", historyJsonList);

        if (!historyJsonList.isEmpty()) {
            // 모든 JSON을 한 번에 파싱
            List<RedisStockPriceHistory> historyList = new ArrayList<>();

            for (String historyJson : historyJsonList) {
                try {
                    RedisStockPriceHistory history = parseRedisHistory(historyJson, stockId);
                    if (history != null) { // null 체크 추가
                        historyList.add(history);
                    }
                } catch (Exception e) {
                    log.error("히스토리 파싱 실패: stockId={}, json={}", stockId, historyJson, e);
                }
            }

            // 벌크 INSERT
            if (!historyList.isEmpty()) {
                ecoStockPriceHistoryService.saveBatch(historyList);
                log.info("가격 히스토리 벌크 저장 완료: stockId={}, count={}", stockId, historyList.size());
            }

            // Redis 데이터 클리어
            chartDataSecondRedisRepository.clearPriceHistoryData(stockId);
        }
    }

    private RedisStockPriceHistory parseRedisHistory(String historyJson, Long stockId) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(historyJson);

            String tradeType = node.has("trade_type") ? node.get("trade_type").asText() : "UNKNOWN";
            Integer tradeQuantity =
                node.has("trade_quantity") ? node.get("trade_quantity").asInt() : 0;

            // tradeType에 따라 sellCount, buyCount 분배
            Integer sellCount = 0;
            Integer buyCount = 0;

            if ("SELL".equals(tradeType)) {
                sellCount = tradeQuantity;
            } else if ("BUY".equals(tradeType)) {
                buyCount = tradeQuantity;
            }
            // AUTO_INCREASE는 둘 다 0

            return RedisStockPriceHistory.builder()
                .stockPriceHistoryId(node.get("stock_price_history_id").asLong())
                .ecoStockId(stockId)
                .time(
                    Instant.ofEpochSecond(node.get("time").asLong()).atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .price(node.get("price").asDouble())
                .quantity(node.has("quantity") ? node.get("quantity").asLong() : null)
                .tradeQuantity(tradeQuantity)
                .tradeType(tradeType)
                .sellCount(sellCount)
                .buyCount(buyCount)
                .build();
        } catch (Exception e) {
            log.error("JSON 파싱 실패: {}", historyJson, e);
            return null;
        }
    }

    private void insertStockMinutePriceHistory(Long stockId, LocalDateTime targetTime,
        OhlcDto ohlcDto,
        VolumeDto volumeDto) {

        StockMinutePriceHistory history = StockMinutePriceHistory.builder()
            .ecoStockId(stockId)
            .stockTimeMinute(targetTime)
            .stockTimeEpoch(StockChartUtil.convertLocalDateTimeToEpoch(targetTime))
            .open(ohlcDto.open())
            .high(ohlcDto.high())
            .low(ohlcDto.low())
            .close(ohlcDto.close())
            .value(volumeDto.value())
            .sellCount(volumeDto.sellCount())
            .buyCount(volumeDto.buyCount())
            .color(volumeDto.color())
            .build();

        log.debug("Inserting stock minute price history: {}", history);
        ecoStockMinutePriceHistoryService.insert(history);
    }
}