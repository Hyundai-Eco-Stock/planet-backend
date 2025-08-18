package org.phoenix.planet.dto.order.response;

/**
 * 주문 생성 응답 DTO
 */
public record CreateOrderResponse(
        String orderNumber,  // 생성된 주문 번호 (TossPayments orderId로 사용)
        Long finalPayPrice,
        String message
) {
}
