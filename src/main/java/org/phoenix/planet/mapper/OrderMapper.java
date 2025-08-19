package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.order.raw.OrderHistory;
import org.phoenix.planet.dto.order.raw.OrderProduct;

@Mapper
public interface OrderMapper {

    // 주문 기본 정보 저장
    void insertOrderHistory(OrderHistory orderHistory);

    // 주문 상품 정보 일괄 저장
    void insertOrderProduct(OrderProduct orderProduct);

}
