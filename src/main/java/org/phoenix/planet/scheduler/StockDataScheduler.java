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
import org.phoenix.planet.repository.ChartDataSecondRedisRepository;
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
    private final ChartDataSecondRedisRepository chartDataSecondRedisRepository;

    private static final int SECOND5TIME = 5;
    private static final String FIVESECOND  = "onfiveSecond-channel";
    private static final String OTHERSECOND  = "onOtherSecond-channel";
    private static final String MINUTE = "onMinute-channel";

    @Scheduled(cron = "0/5 * * * * *") // 1분마다
    @DistributedScheduled(lockKey = "ecoStock:price:5:update:lock")
    public void broadcastStockData() {

        LocalDateTime now = LocalDateTime.now();

        int second = now.getSecond();

        int floored = (second / 5) * 5;  // 0~59초 → 0,5,10,...,55 로 변환

        LocalDateTime nowTime = now
            .withSecond(floored)
            .withNano(0);

        LocalDateTime targetTime = nowTime
            .minusSeconds(5)   // 초를 0으로 // 초를 5초로 빼기
            .withNano(0);

        List<EcoStockUpdatePriceRecord> ecoStockUpdatePriceRecords =
                ecoStockService.findAllHistory(targetTime);

        log.info(" 로우 값 {} ", ecoStockUpdatePriceRecords);
        for (EcoStockUpdatePriceRecord record : ecoStockUpdatePriceRecords) {
            Long stockId = record.ecoStockId();

            StockCalculationResult stockCalculationResult = ecoStockPriceHistoryService.calculatePriceHistory(
                record, nowTime);

            StockData stockData = stockCalculationResult.stockData();
            log.info("stockData:{}", stockData);

            int result = ecoStockPriceHistoryService.saveIfNotExists(stockData);

            if (result == 0) {
                log.info("이미 존재하는 데이터입니다");
                continue;
            }

            //업데이트 자제 할 수도
            ecoStockService.updateQuantityById(stockId, stockCalculationResult.updateQuantity());

            // 정각(0초) 처리
            if (second == 0) {
                handleOnMinute(nowTime, stockId, stockCalculationResult, stockData, record);
                return;
            }

            // 5초 처리
            if (second == 5) {
                handleOnFiveSecond(nowTime, stockId, stockCalculationResult, stockData, record);
                return;
            }

            handleOnOtherSecond(nowTime, stockId, stockCalculationResult, stockData, record);
        }
    }

    private void handleOnFiveSecond(LocalDateTime nowTime, Long stockId,
        StockCalculationResult stockCalculationResult
        , StockData stockData, EcoStockUpdatePriceRecord record) {

        OhlcDto ohlcDto = StockChartUtil.convertSingleToOhlc(stockData, record.beforePrice());

        VolumeDto volumeDto = StockChartUtil.convertSingleToVolume(stockData);

        if (!chartDataRedisRepository.existOhlcDto(stockId)
            || !chartDataRedisRepository.existVolumeDto(stockId)) {

            log.info("Redis 데이터 재초기화 필요 (stockId={})", stockId);

            ecoStockPriceHistoryService.updateRedisData(stockId);

        } else {
            log.debug("Redis에 실시간 데이터 추가 (stockId={})", stockId);
            chartDataRedisRepository.pushSingleOhlcData(stockId, ohlcDto);
            chartDataRedisRepository.pushSingleVolumeData(stockId, volumeDto);

            chartDataSecondRedisRepository.pushSecondOhlcDtoData(stockId, SECOND5TIME,nowTime , ohlcDto);
            chartDataSecondRedisRepository.pushSecondVolumeDtoData(stockId, SECOND5TIME,nowTime ,volumeDto);
        }

        ChartSingleDataResponse chartData = ChartSingleDataResponse.builder()
            .ecoStockId(stockId)
            .volumeData(volumeDto)
            .ohlcData(ohlcDto)
            .build();

        stockDataPublish.pushData(chartData);
    }
    //현재 가격만 주면됨
    // redis만 업데이트하면됨
    // 레디스에 현재 1분마다 각 캐싱해서
    // 가격 모두 비교 low,high  각각 크고 작은 것만 따로 저장
    // 저장된 데이터 보내줘야할 듯???
    // DB 추가되어야한다
    //전부 이거자
    private void handleOnOtherSecond(LocalDateTime nowTime, Long stockId,
        StockCalculationResult stockCalculationResult, StockData stockData,
        EcoStockUpdatePriceRecord record) {

        //레디스 조회
        OhlcDto beforeOhlcDto = chartDataSecondRedisRepository.SecondOhlcDtoData(stockId,
                    nowTime,SECOND5TIME);
        VolumeDto beforeVolumeDto = chartDataSecondRedisRepository.SecondVolumeDtoData(stockId,
                            nowTime,SECOND5TIME);
        //OhlcDto 비교 로직
        long high = Math.max(beforeOhlcDto.high(), stockData.getStockPrice());
        long low = Math.min(beforeOhlcDto.low(), stockData.getStockPrice());

        //VolumeDto 비교 로직
        VolumeDto volumeDto = StockChartUtil.updateVolumeDto(beforeVolumeDto, stockData);

        OhlcDto ohlcDto = new OhlcDto(stockData.getStockPriceHistoryId(),
            StockChartUtil.convertLocalDateTimeToEpoch(nowTime)
            , beforeOhlcDto.open(), high, low, beforeOhlcDto.close(),beforeOhlcDto.isEmpty());

        if (!chartDataRedisRepository.existOhlcDto(stockId)
            || !chartDataRedisRepository.existVolumeDto(stockId)) {

            log.info("Redis 데이터 재초기화 필요 (stockId={})", stockId);

            ecoStockPriceHistoryService.updateRedisData(stockId);

        } else {
            log.debug("Redis에 실시간 데이터 추가 (stockId={})", stockId);
            chartDataSecondRedisRepository.pushSecondOhlcDtoData(stockId, SECOND5TIME,nowTime , ohlcDto);
            chartDataSecondRedisRepository.pushSecondVolumeDtoData(stockId, SECOND5TIME,nowTime ,volumeDto);
        }

        ChartSingleDataResponse chartData = ChartSingleDataResponse.builder()
            .ecoStockId(stockId)
            .volumeData(volumeDto)
            .ohlcData(ohlcDto)
            .build();
        stockDataPublish.pushData(chartData);
    }

    private void handleOnMinute(LocalDateTime nowTime, Long stockId,
        StockCalculationResult stockCalculationResult
        , StockData stockData, EcoStockUpdatePriceRecord record) {

        // 레디스에서 가장
        //low,high  비교  후
        //close price
        //전부 1분봉 짜리 redis 저장
        //

        OhlcDto beforeOhlcDto = chartDataSecondRedisRepository.SecondOhlcDtoData(stockId,
                    nowTime,SECOND5TIME);
        VolumeDto beforeVolumeDto = chartDataSecondRedisRepository.SecondVolumeDtoData(stockId,
                            nowTime,SECOND5TIME);
        //OhlcDto 비교 로직
        long high = Math.max(beforeOhlcDto.high(), stockData.getStockPrice());
        long low = Math.min(beforeOhlcDto.low(), stockData.getStockPrice());

        //VolumeDto 비교 로직
        VolumeDto volumeDto = StockChartUtil.updateVolumeDto(beforeVolumeDto, stockData);

        OhlcDto ohlcDto = new OhlcDto(stockData.getStockPriceHistoryId(),
            StockChartUtil.convertLocalDateTimeToEpoch(nowTime)
            , beforeOhlcDto.open(), high, low, beforeOhlcDto.close(),beforeOhlcDto.isEmpty());

        if (!chartDataRedisRepository.existOhlcDto(stockId)
            || !chartDataRedisRepository.existVolumeDto(stockId)) {

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