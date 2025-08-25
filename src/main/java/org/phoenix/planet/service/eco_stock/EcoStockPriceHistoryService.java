package org.phoenix.planet.service.eco_stock;

import org.phoenix.planet.dto.eco_stock.raw.EcoStockUpdatePriceRecord;
import org.phoenix.planet.dto.eco_stock.raw.StockCalculationResult;
import org.phoenix.planet.dto.eco_stock.raw.StockData;
import org.phoenix.planet.dto.eco_stock.response.ChartDataResponse;

import java.time.LocalDateTime;

public interface EcoStockPriceHistoryService {

    ChartDataResponse findAllPrice(Long ecoStockId);

    ChartDataResponse updateRedisData(Long ecoStockId);
}
