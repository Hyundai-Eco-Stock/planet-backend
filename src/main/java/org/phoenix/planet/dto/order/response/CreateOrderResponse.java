package org.phoenix.planet.dto.order.response;

import org.phoenix.planet.dto.order.raw.PickupStoreInfo;

import java.util.List;

/**
 * 주문 생성 응답 DTO
 */
public record CreateOrderResponse(
        String orderNumber,  // 생성된 주문 번호 (TossPayments orderId로 사용)
        Long totalAmount,
        List<PickupStoreInfo> availablePickupStores,  // 에코딜 상품 구매 시, 픽업 가능한 매장 리스트
        String message
) {
}
