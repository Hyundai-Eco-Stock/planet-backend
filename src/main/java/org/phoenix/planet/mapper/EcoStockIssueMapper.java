package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EcoStockIssueMapper {

    int insert(
        @Param("memberId") long memberId,
        @Param("ecoStockId") long ecoStockId);

}