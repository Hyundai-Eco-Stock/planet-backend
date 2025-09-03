package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.constant.OrderStatus;
import org.phoenix.planet.dto.order.raw.OrderHistory;

import java.time.LocalDateTime;
import org.phoenix.planet.dto.pickup.raw.OrderQrHeader;
import org.phoenix.planet.dto.pickup.raw.ProductQrInfo;

@Mapper
public interface OrderHistoryMapper {

    void insert(OrderHistory orderHistory);

    OrderHistory findById(@Param("orderHistoryId") Long orderHistoryId);

    OrderHistory findByOrderNumber(@Param("orderNumber") String orderNumber);

    String findOrderNumberById(@Param("orderHistoryId") Long orderHistoryId);

    void updateQRCodeUrl(@Param("orderHistoryId") Long orderHistoryId, @Param("ecoDealQrUrl") String ecoDealQrUrl);

    int updateDonationPrice(@Param("orderHistoryId") Long orderHistoryId, @Param("donationPrice") Long donationPrice);

    int updateOrderStatus(
            @Param("orderHistoryId") Long orderHistoryId,
            @Param("orderStatus") OrderStatus orderStatus,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    OrderQrHeader selectOrderQrHeader(Long orderHistoryId);

    List<ProductQrInfo> selectOrderProducts(Long orderHistoryId);

}