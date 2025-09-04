package org.phoenix.planet.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.DistributedScheduled;
import org.phoenix.planet.dto.eco_stock.raw.EcoStockUpdatePriceRecord;
import org.phoenix.planet.dto.eco_stock.raw.OhlcDto;
import org.phoenix.planet.dto.eco_stock.raw.StockCalculationResult;
import org.phoenix.planet.dto.eco_stock.raw.StockData;
import org.phoenix.planet.dto.eco_stock.raw.VolumeDto;
import org.phoenix.planet.dto.eco_stock.response.ChartSingleDataResponse;
import org.phoenix.planet.repository.ChartDataRedisRepository;
import org.phoenix.planet.service.eco_stock.EcoStockPriceHistoryService;
import org.phoenix.planet.service.eco_stock.EcoStockService;
import org.phoenix.planet.service.websocket.StockDataPublish;
import org.phoenix.planet.util.ecoStock.StockChartUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockDataScheduler {

    private final StockDataPublish stockDataPublish;
    private final EcoStockPriceHistoryService ecoStockPriceHistoryService;
    private final EcoStockService ecoStockService;
    private final ChartDataRedisRepository chartDataRedisRepository;

    @Scheduled(cron = "0 * * * * *") // 1분마다
    @DistributedScheduled(lockKey = "ecoStock:price:update:lock")
    public void broadcastStockData() {

        LocalDateTime time = LocalDateTime.now()
            .withSecond(0)   // 초를 0으로
            .withNano(0);

        LocalDateTime targetTime = time
            .minusMinutes(1)
            .withSecond(0)   // 초를 0으로
            .withNano(0);

        List<EcoStockUpdatePriceRecord> ecoStockUpdatePriceRecords =
            ecoStockService.findAllHistory(targetTime);

        log.trace(" 로우 값 {} ", ecoStockUpdatePriceRecords);
        for (EcoStockUpdatePriceRecord record : ecoStockUpdatePriceRecords) {
            Long stockId = record.ecoStockId();

            StockCalculationResult stockCalculationResult = ecoStockPriceHistoryService.calculatePriceHistory(
                record, time);

            StockData stockData = stockCalculationResult.stockData();
            log.debug("stockData:{}", stockData);

            int result = ecoStockPriceHistoryService.saveIfNotExists(stockData);

            if (result == 0) {
                continue;
            }

            ecoStockService.updateQuantityById(stockId, stockCalculationResult.updateQuantity());

            OhlcDto ohlcDto = StockChartUtil.convertSingleToOhlc(stockData, record.beforePrice());

            VolumeDto volumeDto = StockChartUtil.convertSingleToVolume(stockData);

            if (!chartDataRedisRepository.existOhlcDto(stockId)
                || !chartDataRedisRepository.existVolumeDto(stockId)
                || chartDataRedisRepository.checkTimestamp(stockId, ohlcDto)) {

                log.info("Redis 데이터 재초기화 필요 (stockId={})", stockId);

                ecoStockPriceHistoryService.updateRedisData(stockId);

            } else {
                log.debug("Redis에 실시간 데이터 추가 (stockId={})", stockId);
                chartDataRedisRepository.pushSingleOhlcData(stockId, ohlcDto);
                chartDataRedisRepository.pushSingleVolumeData(stockId, volumeDto);
            }

            ChartSingleDataResponse chartData = ChartSingleDataResponse.builder()
                .ecoStockId(stockId)
                .volumeData(volumeDto)
                .ohlcData(ohlcDto)
                .build();
            stockDataPublish.pushData(chartData);
        }
    }
}