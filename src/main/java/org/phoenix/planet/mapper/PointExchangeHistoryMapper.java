package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PointExchangeHistoryMapper {

    void insert(@Param("memberId") Long memberId, @Param("pointPrice") int point_price);

}
