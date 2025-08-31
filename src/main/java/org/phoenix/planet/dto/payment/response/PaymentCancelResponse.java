package org.phoenix.planet.dto.payment.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 결제 취소 응답 DTO
 */
public record PaymentCancelResponse(
        boolean success,
        String message,
        Long orderId,
        String orderNumber,
        Long cancelAmount,
        Long refundedPoints,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime cancelledAt
) {

    public static PaymentCancelResponse success(
            Long orderId,
            String orderNumber,
            Long cancelAmount,
            Long refundedPoints,
            String message
    ) {
        return new PaymentCancelResponse(
                true,
                message,
                orderId,
                orderNumber,
                cancelAmount,
                refundedPoints,
                LocalDateTime.now()
        );
    }

    public static PaymentCancelResponse error(String message) {
        return new PaymentCancelResponse(
                false,
                message,
                null,
                null,
                null,
                null,
                null
        );
    }

}
