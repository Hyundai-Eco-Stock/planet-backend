package org.phoenix.planet.service.eco_stock;

import org.phoenix.planet.dto.eco_stock.raw.StockMinutePriceHistory;

public interface EcoStockMinutePriceHistoryService {

    void insert(StockMinutePriceHistory smph);
}
