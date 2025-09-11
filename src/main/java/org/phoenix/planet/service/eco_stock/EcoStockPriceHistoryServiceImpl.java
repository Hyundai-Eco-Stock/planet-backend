package org.phoenix.planet.service.eco_stock;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.raw.OhlcDto;
import org.phoenix.planet.dto.eco_stock.raw.RedisStockPriceHistory;
import org.phoenix.planet.dto.eco_stock.raw.StockMinutePriceHistory;
import org.phoenix.planet.dto.eco_stock.raw.VolumeDto;
import org.phoenix.planet.dto.eco_stock.response.ChartDataResponse;
import org.phoenix.planet.mapper.EcoStockMinutePriceHistoryMapper;
import org.phoenix.planet.mapper.EcoStockPriceHistoryMapper;
import org.phoenix.planet.repository.ChartDataRedisRepository;
import org.phoenix.planet.repository.ChartDataSecondRedisRepository;
import org.phoenix.planet.util.ecoStock.StockChartUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EcoStockPriceHistoryServiceImpl implements EcoStockPriceHistoryService {
    private final EcoStockPriceHistoryMapper ecoStockPriceHistoryMapper;
    private final ChartDataRedisRepository chartDataRedisRepository;
    private final EcoStockMinutePriceHistoryMapper ecoStockMinutePriceHistoryMapper;
    private final ChartDataSecondRedisRepository chartDataSecondRedisRepository;

    @Override
    public void save(Long stockId, double newPrice) {

        ecoStockPriceHistoryMapper.save(stockId, newPrice);
    }

    @Override
    public ChartDataResponse findAllPrice(Long ecoStockId) {

        List<OhlcDto> ohlcData = chartDataRedisRepository.findAllOhlcData(ecoStockId);

        List<VolumeDto> volumeData = chartDataRedisRepository.findAllVolumeData(ecoStockId);

        LocalDateTime now = LocalDateTime.now();

        OhlcDto newOhlcDto=chartDataSecondRedisRepository.SecondOhlcDtoData(ecoStockId,now);

        VolumeDto newVolumeDto = chartDataSecondRedisRepository.SecondVolumeDtoData(ecoStockId,now);

        if (ohlcData == null || ohlcData.isEmpty()
                || volumeData == null || volumeData.isEmpty()) {

            log.info("findAllPrice cache miss !!!!");

            ChartDataResponse chartDataResponse =  updateRedisData(ecoStockId);
            chartDataResponse.setLastOhlcData(newOhlcDto);
            chartDataResponse.setLastVolumeData(newVolumeDto);
            return chartDataResponse;
        }

        log.info("findAllPrice cache hit");

        return ChartDataResponse.builder()
                .ohlcData(ohlcData)
                .volumeData(volumeData)
                .lastOhlcData(newOhlcDto)
                .lastVolumeData(newVolumeDto)
                .build();
    }

    @Override
    public ChartDataResponse updateRedisData(Long ecoStockId) {

        List<StockMinutePriceHistory> originalData =ecoStockMinutePriceHistoryMapper.findAllPriceHistory(ecoStockId);

        if (originalData == null || originalData.isEmpty()) {

            log.info("noting in originalData");

            return ChartDataResponse.builder()
                    .ohlcData(List.of())
                    .volumeData(List.of())
                    .build();
        }

        List<OhlcDto> ohlcData = StockChartUtil.convertMinuteToOhlc(originalData);

        List<VolumeDto> volumeData = StockChartUtil.convertMinuteToVolume(originalData);

        chartDataRedisRepository.upsertOhlcBatchNoDup(ecoStockId, ohlcData);

        chartDataRedisRepository.upsertVolumeBatchNoDup(ecoStockId, volumeData);

        return ChartDataResponse.builder()
                .ohlcData(ohlcData)
                .volumeData(volumeData)
                .build();
    }

    @Transactional
    @Override
    public void saveBatch(List<RedisStockPriceHistory> historyList) {
        int insertedCount = ecoStockPriceHistoryMapper.insertBatch(historyList);
        if (insertedCount != historyList.size()) {
            log.warn("벌크 인서트 부분 실패: 예상={}, 실제={}",
                historyList.size(), insertedCount);
        }
    }
}