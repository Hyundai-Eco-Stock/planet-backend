package org.phoenix.planet.dto.payment.raw;

public record PartialCancelCalculation(
        Long cancelAmount,      // 총 취소 금액
        Long refundedPoints,    // 환불 포인트
        Long refundedDonation   // 환불 기부금
) {
}
