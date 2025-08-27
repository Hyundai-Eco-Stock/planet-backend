package org.phoenix.planet.dto.payment.response;

public record PaymentConfirmResponse(
        boolean success,
        String message,
        PaymentResultData data
) {

    public record PaymentResultData(
            String orderId,
            String orderNumber,
            String paymentKey,
            Integer totalAmount,
            String paymentMethod,
            String paymentStatus,
            String qrCodeData,      // 픽업 주문인 경우 QR 코드
            String approvedAt,      // 결제 완료 시간
            String receiptUrl       // 영수증 URL
    ) {}

}
