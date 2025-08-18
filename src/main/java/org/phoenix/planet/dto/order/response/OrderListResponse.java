package org.phoenix.planet.dto.order.response;

import org.phoenix.planet.constant.OrderStatus;

import java.time.LocalDateTime;

/**
 * 주문 목록 응답 DTO
 */
public record OrderListResponse(
        Long orderHistoryId,
        String orderNumber,
        OrderStatus orderStatus,
        Long finalPayPrice,
        LocalDateTime createdAt,
        String firstProductName,
        Integer totalProductCount,
        String firstProductImageUrl
) {
}
