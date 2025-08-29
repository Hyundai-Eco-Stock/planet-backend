package org.phoenix.planet.dto.payment.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 부분 취소 요청 DTO
 */
public record PartialCancelRequest(
        @NotEmpty(message = "취소할 상품이 없습니다.") @Valid List<CancelItem> cancelItems,    // 취소할 상품 목록 (상품 단위로만)
        String cancelReason,             // 취소 사유
        Boolean refundDonation          // 기부금 환불 여부
) {

    /**
     * 취소할 개별 상품 정보
     */
    public record CancelItem(
            @NotNull(message = "주문 상품 ID는 필수입니다.") Long orderProductId    // order_product 테이블의 ID
    ) {}

}
