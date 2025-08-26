package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.order.raw.OrderProduct;

@Mapper
public interface OrderProductMapper {

    void insert(OrderProduct orderProduct);

}
