package org.phoenix.planet.dto.payment.request;

/**
 * 결제 취소 요청 DTO
 */
public record PaymentCancelReqeust(
        String cancelReason  // 취소 사유
) {
}
