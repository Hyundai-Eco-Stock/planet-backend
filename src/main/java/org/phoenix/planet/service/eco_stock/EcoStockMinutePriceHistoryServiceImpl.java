package org.phoenix.planet.service.eco_stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.raw.StockMinutePriceHistory;
import org.phoenix.planet.mapper.EcoStockMinutePriceHistoryMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EcoStockMinutePriceHistoryServiceImpl implements EcoStockMinutePriceHistoryService {

    private final EcoStockMinutePriceHistoryMapper ecoStockMinutePriceHistoryMapper;

    @Override
    public void insert(StockMinutePriceHistory smph) {
        ecoStockMinutePriceHistoryMapper.insert(smph);
    }
}
