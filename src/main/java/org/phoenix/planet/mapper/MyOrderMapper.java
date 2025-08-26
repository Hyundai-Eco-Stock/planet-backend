package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.order.response.MyOrderResponse;

@Mapper
public interface MyOrderMapper {

    List<MyOrderResponse> findMyOrders(Long memberId);
}
