package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.order.raw.OrderHistory;

@Mapper
public interface OrderHistoryMapper {

    void insert(OrderHistory orderHistory);

    OrderHistory findById(@Param("orderHistoryId") Long orderHistoryId);

    OrderHistory findByOrderNumber(@Param("orderNumber") String orderNumber);

    String findOrderNumberById(@Param("orderHistoryId") Long orderHistoryId);

    void updateQRCodeUrl(@Param("orderHistoryId") Long orderHistoryId, @Param("ecoDealQrUrl") String ecoDealQrUrl);

}