package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OfflineShopMapper {

    String selectTypeById(Long shopId);
}
