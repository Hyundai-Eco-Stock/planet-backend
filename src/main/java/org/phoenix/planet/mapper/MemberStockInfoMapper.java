package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock_info.response.MemberStockInfoWithDetail;

@Mapper
public interface MemberStockInfoMapper {

    MemberStockInfo findPersonalStockInfoById(@Param("memberId") Long memberId,
        @Param("ecoStockId") Long ecoStockId);

    List<MemberStockInfoWithDetail> findAllPersonalStockInfoByMemberId(Long memberId);

    void updateOrInsert(
        @Param("memberId") long memberId,
        @Param("ecoStockId") long ecoStockId,
        @Param("quantity") int quantity);
}
