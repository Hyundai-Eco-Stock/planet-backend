package org.phoenix.planet.dto.payment.response;

import org.phoenix.planet.constant.PaymentMethod;
import org.phoenix.planet.constant.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse(
        String paymentKey,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        LocalDateTime createdAt
) {
}
