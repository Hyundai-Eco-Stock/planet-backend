package org.phoenix.planet.service.eco_stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.raw.*;
import org.phoenix.planet.dto.eco_stock.response.ChartDataResponse;
import org.phoenix.planet.mapper.EcoStockPriceHistoryMapper;
import org.phoenix.planet.repository.ChartDataRedisRepository;
import org.phoenix.planet.util.ecoStock.StockChartUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EcoStockPriceHistoryServiceImpl implements EcoStockPriceHistoryService {
    private final EcoStockPriceHistoryMapper ecoStockPriceHistoryMapper;
    private final ChartDataRedisRepository chartDataRedisRepository;
    private static final double ALPHA = 0.5;   // ESG 행동의 가격 반영 강도
    private static final double BETA = 0.3;   // 발행량 희석 강도
    private static final double GAMMA = 0.01;  // 매수/매도 차이에 따른 변동 강도
    private static final Random random = new Random();

    @Override
    public ChartDataResponse findAllPrice(Long ecoStockId) {

        List<OhlcDto> ohlcData = chartDataRedisRepository.findAllOhlcData(ecoStockId);

        List<VolumeDto> volumeData = chartDataRedisRepository.findAllVolumeData(ecoStockId);

        if (ohlcData == null || ohlcData.isEmpty()
                || volumeData == null || volumeData.isEmpty()
                || chartDataRedisRepository.checkTimestamp(ecoStockId, ohlcData.getLast())) {

            log.info("findAllPrice cache miss !!!!");

            return updateRedisData(ecoStockId);
        }

        log.info("findAllPrice cache hit");

        return ChartDataResponse.builder()
                .ohlcData(ohlcData)
                .volumeData(volumeData)
                .build();
    }

    @Override
    public ChartDataResponse updateRedisData(Long ecoStockId) {

        List<StockData> originalData = ecoStockPriceHistoryMapper.findAllPriceHistory(ecoStockId);

        if (originalData == null || originalData.isEmpty()) {

            log.info("noting in originalData");

            return ChartDataResponse.builder()
                    .ohlcData(List.of())
                    .volumeData(List.of())
                    .build();
        }

//        List<StockData> filledData = StockChartUtil.fillMissingMinutes(originalData);

        List<OhlcDto> ohlcData = StockChartUtil.convertToOhlc(originalData);

        List<VolumeDto> volumeData = StockChartUtil.convertToVolume(originalData);
        log.info("upsertOhlcBatchNoDup first 10: {}",
                ohlcData.stream().limit(10).collect(Collectors.toList()));
        // 2. Redis에 배치 저장 (기존 방식과 동일)
        chartDataRedisRepository.upsertOhlcBatchNoDup(ecoStockId, ohlcData);

        chartDataRedisRepository.upsertVolumeBatchNoDup(ecoStockId, volumeData);

        return ChartDataResponse.builder()
                .ohlcData(ohlcData)
                .volumeData(volumeData)
                .build();
    }

    /**
     * 계산 로직 요메소드만 private은 내부 메소드로 안 봐도됨
     */
    @Override
    public StockCalculationResult calculatePriceHistory(EcoStockUpdatePriceRecord record, LocalDateTime time) {

        long basePrice = determineBasePrice(record);

        int cumulativeTotal = record.quantity();

        NoiseNewPriceData noiseNewPriceData = calculateTradeNoise(record);

        long newPrice = calculateNewPrice(basePrice, cumulativeTotal, noiseNewPriceData);

        int updateQuantity = calculateUpdateQuantity(cumulativeTotal, noiseNewPriceData);

        StockData stockData = createStockData(record, time, newPrice, noiseNewPriceData);

        return StockCalculationResult.builder()
                .updateQuantity(updateQuantity)
                .stockData(stockData)
                .build();
    }

    private NoiseNewPriceData calculateTradeNoise(EcoStockUpdatePriceRecord record) {
        int baseSellCount = record.transactionHistoryCount();
        int baseBuyCount = record.stockIssueCount();

        int sellNoiseRange = Math.max(random.nextInt(100), baseSellCount / 10);
        int buyNoiseRange = Math.max(random.nextInt(300), baseBuyCount / 10);

        return new NoiseNewPriceData(sellNoiseRange, buyNoiseRange);
    }

    private long calculateNewPrice(long basePrice, int cumulativeTotal, NoiseNewPriceData noiseNewPriceData) {
        double price = basePrice
                + ALPHA * Math.log(1 + noiseNewPriceData.buyNoiseRange())
                - BETA * Math.log(1 + cumulativeTotal)
                + GAMMA * (noiseNewPriceData.buyNoiseRange() - noiseNewPriceData.sellNoiseRange());

        long newPrice = (long) Math.ceil(price);

        if (newPrice < 0) {
            log.info("calculate price minus");
            newPrice = 1;
        }

        return newPrice;
    }

    private int calculateUpdateQuantity(int cumulativeTotal, NoiseNewPriceData noiseNewPriceData) {
        return cumulativeTotal + noiseNewPriceData.buyNoiseRange() - noiseNewPriceData.sellNoiseRange();
    }

    private StockData createStockData(EcoStockUpdatePriceRecord record, LocalDateTime time,
                                      long newPrice, NoiseNewPriceData noiseNewPriceData) {
        return new StockData(
                null, // stockPriceHistoryId - DB에서 생성될 것
                record.ecoStockId(),
                time,
                newPrice,
                noiseNewPriceData.sellNoiseRange(),
                noiseNewPriceData.buyNoiseRange()
        );
    }

    private long determineBasePrice(EcoStockUpdatePriceRecord record) {
        return (record.beforePrice() == null || record.beforePrice() == 0)
                ? record.initPrice()
                : record.beforePrice();
    }
}