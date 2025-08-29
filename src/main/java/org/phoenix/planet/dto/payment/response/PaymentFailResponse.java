package org.phoenix.planet.dto.payment.response;

public record PaymentFailResponse(
        boolean success,
        String message,
        String errorCode,
        String errorMessage,
        String orderId
) {

    public PaymentFailResponse(String message, String errorCode, String errorMessage, String orderId) {
        this(false, message, errorCode, errorMessage, orderId);
    }

}
