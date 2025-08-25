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
}