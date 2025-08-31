package org.phoenix.planet.dto.payment.request;

public record TossPaymentsCancelRequest(
        Integer cancelAmount,  // Toss API는 Integer 사용
        String cancelReason
) {
}
