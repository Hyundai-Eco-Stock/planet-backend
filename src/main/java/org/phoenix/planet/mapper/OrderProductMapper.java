package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.order.raw.OrderProduct;

import java.util.List;

@Mapper
public interface OrderProductMapper {

    void insert(OrderProduct orderProduct);

}
