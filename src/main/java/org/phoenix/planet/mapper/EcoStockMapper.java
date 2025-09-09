package org.phoenix.planet.mapper;

import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.dto.eco_stock.raw.EcoStockUpdatePriceRecord;
import org.phoenix.planet.dto.eco_stock.raw.EcoStockWithLastPrice;
import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock.raw.PointResult;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;

import java.util.List;
import org.phoenix.planet.dto.eco_stock.response.UnifiedUpdateResult;

@Mapper
public interface EcoStockMapper {

    EcoStock selectById(Long id);

    List<EcoStock> findAll();

    List<EcoStockUpdatePriceRecord> findAllHistory(@Param("targetTime") LocalDateTime targetTime);

    void updateQuantityById(@Param("stockId") Long stockId, @Param(("updateQuantity")) int updateQuantity);

    void callSellStockProcedure(@Param("memberId") Long memberId, @Param("request") SellStockRequest sellStockRequest);

    PointResult callSellStockUserTransaction(
        @Param("loginMemberId") Long loginMemberId,
        @Param("result") UnifiedUpdateResult result,
        @Param("sellQuantity") Integer sellQuantity,
        @Param("memberStockInfo") MemberStockInfo memberStockInfo,
        @Param("pointResult") PointResult pointResult
    );

    List<EcoStockWithLastPrice> findAllWithLastPrice();

    EcoStockWithLastPrice findAllWithLastPriceByStockId(@Param("stockId") Long stockId);
}
