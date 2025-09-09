package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.eco_stock.response.UnifiedUpdateResult;

@Mapper
public interface TransactionHistoryMapper {

    void save(@Param("memberStockInfoId") Long memberStockInfoId,
        @Param("result") UnifiedUpdateResult result,
        @Param("sellCount") Integer sellCount);
}
