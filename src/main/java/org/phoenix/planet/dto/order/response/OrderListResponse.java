package org.phoenix.planet.dto.order.response;

import java.time.LocalDateTime;
import org.phoenix.planet.constant.order.OrderStatus;

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
