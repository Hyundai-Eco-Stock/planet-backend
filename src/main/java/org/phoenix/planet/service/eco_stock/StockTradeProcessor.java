package org.phoenix.planet.service.eco_stock;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;
import org.phoenix.planet.dto.eco_stock.response.ChartSingleDataResponse;
import org.phoenix.planet.dto.eco_stock.response.UnifiedUpdateResult;
import org.phoenix.planet.repository.ChartDataSecondRedisRepository;
import org.phoenix.planet.service.websocket.StockDataPublish;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockTradeProcessor {

    private final ChartDataSecondRedisRepository chartDataSecondRedisRepository;
    private final StockDataPublish stockDataPublish;

    public UnifiedUpdateResult executeSellTradeAndBroadcast(Long ecoStockId, Integer tradCount) {

        LocalDateTime now = LocalDateTime.now();

        UnifiedUpdateResult result = chartDataSecondRedisRepository.processTradeWithChart(ecoStockId, tradCount, now);

        ChartSingleDataResponse chartData = ChartSingleDataResponse.builder()
            .ecoStockId(ecoStockId)
            .volumeData(result.getVolumeDto())
            .ohlcData(result.getOhlcDto())
            .build();

        stockDataPublish.pushData(chartData);

        return result;
    }

    public UnifiedUpdateResult executeIssueTradeAndBroadcast(Long ecoStockId, Integer tradCount) {

        LocalDateTime now = LocalDateTime.now();

        UnifiedUpdateResult result = chartDataSecondRedisRepository.processTradeWithChart(ecoStockId, -tradCount, now);

        ChartSingleDataResponse chartData = ChartSingleDataResponse.builder()
            .ecoStockId(ecoStockId)
            .volumeData(result.getVolumeDto())
            .ohlcData(result.getOhlcDto())
            .build();

        stockDataPublish.pushData(chartData);
        return result;
    }
}