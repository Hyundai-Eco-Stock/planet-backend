package org.phoenix.planet.mapper;


import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.eco_stock.raw.StockMinutePriceHistory;

@Mapper
public interface EcoStockMinutePriceHistoryMapper {

    void insert(StockMinutePriceHistory smph);

    List<StockMinutePriceHistory> findAllPriceHistory(Long ecoStockId);
}
