package org.phoenix.planet.dto.order.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.phoenix.planet.constant.OrderType;

/**
 * 주문 상품 요청 DTO
 * 클라이언트에서 주문할 상품 정보를 받는 DTO
 * 한 주문 내 모든 상품은 동일한 orderType이어야 함
 */
public record OrderProductRequest(
        @NotNull(message = "상품 ID는 필수입니다") @Min(value = 1, message = "상품 ID는 1 이상이어야 합니다") Long productId,
        @NotNull(message = "수량은 필수입니다") @Min(value = 1, message = "수량은 1개 이상이어야 합니다") Integer quantity,
        @NotNull(message = "주문 타입은 필수입니다") OrderType orderType
) {
}
