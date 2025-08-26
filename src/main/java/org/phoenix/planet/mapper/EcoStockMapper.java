package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.dto.eco_stock.raw.EcoStockUpdatePriceRecord;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;

import java.util.List;

@Mapper
public interface EcoStockMapper {

    EcoStock selectById(Long id);

    List<EcoStock> findAll();

    List<EcoStockUpdatePriceRecord> findAllHistory(@Param("targetTime") String targetTime);

    void updateQuantityById(@Param("stockId") Long stockId, @Param(("updateQuantity")) int updateQuantity);

    void callSellStockProcedure(@Param("memberId") Long memberId, @Param("request") SellStockRequest sellStockRequest);
}
