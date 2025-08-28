package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;

@Mapper
public interface MemberStockInfoMapper {
    MemberStockInfo findPersonalStockInfoById(@Param("memberId") Long memberId, @Param("ecoStockId") Long ecoStockId);
}
