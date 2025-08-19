package org.phoenix.planet.dto.order.raw;

import org.phoenix.planet.constant.OrderError;
import org.phoenix.planet.constant.OrderType;
import org.phoenix.planet.dto.order.request.OrderProductRequest;
import org.phoenix.planet.error.order.OrderException;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDraft(
        Long orderId,
        String orderNumber,
        Long memberId,
        List<OrderProductRequest> products,
        Long totalAmount,

        // DELIVERY용 - 기본값: 사용자 정보, 변경 가능
        String deliveryAddress,  // 기본 배송지 혹은 입력 받은 주소
        String recipientName,
        String recipientPhone,

        // PICKUP용
        Long pickupDepartmentStoreId,

        LocalDateTime createdAt,
        LocalDateTime validUntil
) {

    public OrderType getOrderType() {
        if (products == null || products.isEmpty()) {
            throw new OrderException(OrderError.INVALID_ORDER_DATA);
        }

        OrderType firstType = products.getFirst().orderType();

        // 혼합 주문 검증
        boolean hasMixedTypes = products.stream()
                .anyMatch(product -> product.orderType() != firstType);


        if (hasMixedTypes) {
            throw new OrderException(OrderError.MIXED_ORDER_TYPE);
        }

        return firstType;
    }

}
