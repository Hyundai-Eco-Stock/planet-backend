package org.phoenix.planet.mapper;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock_info.response.EcoStockPriceResponse;
import org.phoenix.planet.dto.eco_stock_info.response.MemberStockInfoWithDetail;

@Mapper
public interface MemberStockInfoMapper {

    MemberStockInfo findPersonalStockInfoById(@Param("memberId") Long memberId,
        @Param("ecoStockId") Long ecoStockId);

    void insertMemberStockInfo(
        @Param("memberId") Long memberId,
        @Param("ecoStockId") Long ecoStockId,
        @Param("quantity") Integer quantity,
        @Param("amount") Double amount
    );
    void updateOrInsert(
        @Param("memberId") long memberId,
        @Param("ecoStockId") long ecoStockId,
        @Param("quantity") int quantity);

    void updateMemberStockInfo(
        @Param("memberStockInfoId") Long memberStockInfoId,
        @Param("newQuantity") Integer newQuantity,
        @Param("newAmount") Double newAmount
    );

    List<MemberStockInfoWithDetail> findAllPersonalStockInfoByMemberId(Long memberId);


    List<EcoStockPriceResponse> findAllEcoStockPrice();

    Optional<MemberStockInfo> validateUserStock(
        @Param("memberId") Long memberId,
        @Param("ecoStockId") Long ecoStockId,
        @Param("sellCount") Integer sellCount);
}
