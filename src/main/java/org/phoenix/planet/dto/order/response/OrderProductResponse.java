package org.phoenix.planet.dto.order.response;

import org.phoenix.planet.constant.CancelStatus;
import org.phoenix.planet.constant.OrderType;

/**
 * 주문 상세 조회 시, 상품 정보 담는 DTO
 */
public record OrderProductResponse(
        Long orderProductId,
        Long productId,
        String productName,
        String productImageUrl,
        Long price,  // 결제된 단가
        Integer quantity,
        CancelStatus cancelStatus,
        OrderType orderType,
        Long discountPrice,
        Long originalPrice,  // 원래 상품 가격
        Integer salePercent  // 할인율
) {
}
