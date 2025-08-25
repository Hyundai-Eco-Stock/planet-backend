package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.eco_stock.raw.StockData;

import java.util.List;

@Mapper
public interface EcoStockPriceHistoryMapper {

    List<StockData> findAllPriceHistory(Long ecoStockId);
}
