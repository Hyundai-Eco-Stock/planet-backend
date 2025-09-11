package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.eco_stock.raw.RedisStockPriceHistory;
import org.phoenix.planet.dto.eco_stock.raw.StockData;

import java.util.List;

@Mapper
public interface EcoStockPriceHistoryMapper {

    List<StockData> findAllLatestPrice();

    void save(@Param("stockId") Long stockId,@Param("newPrice") double newPrice);

    int insertBatch(List<RedisStockPriceHistory> historyList);
}
