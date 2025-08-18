package org.phoenix.planet.dto.order.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 주문 취소 요청 DTO
 */
public record CancelOrderRequest(
        @NotEmpty(message = "취소할 상품을 선택해주세요")List<Long> orderProductIds,
        @NotBlank(message = "취소 사유는 필수입니다") String cancelReason
) {
}
