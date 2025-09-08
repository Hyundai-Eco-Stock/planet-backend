package org.phoenix.planet.service.eco_stock;

import java.util.List;
import org.phoenix.planet.dto.eco_stock.raw.RedisStockPriceHistory;
import org.phoenix.planet.dto.eco_stock.response.ChartDataResponse;

public interface EcoStockPriceHistoryService {

    ChartDataResponse findAllPrice(Long ecoStockId);

    ChartDataResponse updateRedisData(Long ecoStockId);

    void save(Long stockId, double newPrice);

    void saveBatch(List<RedisStockPriceHistory> historyList);
}
