package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;

@Mapper
public interface EcoStockMapper {

    EcoStock selectById(Long id);
}
