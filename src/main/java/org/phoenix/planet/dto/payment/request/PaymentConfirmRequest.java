package org.phoenix.planet.dto.payment.request;

import org.phoenix.planet.constant.OrderType;

import java.util.List;

public record PaymentConfirmRequest(
        // TossPayments 필수 파라미터
        String paymentKey,
        String orderId,
        Integer amount,
        String orderName,

        // 고객 정보
        String customerName,
        String customerEmail,
        String customerPhone,

        // 주문 상품 정보
        List<OrderProductInfo> products,

        // 배송 정보 (일반 배송인 경우)
        DeliveryInfo deliveryInfo,

        // 픽업 정보 (픽업 배송인 경우)
        PickupInfo pickupInfo,

        // 결제 옵션
        Integer pointsUsed,      // 사용 포인트
        Integer donationAmount,  // 기부 금액
        OrderType orderType
) {

    public record OrderProductInfo(
            Long productId,
            String productName,
            Integer price,           // 원가
            Integer salePercent,     // 할인율 (0-100)
            Integer quantity,
            Boolean ecoDealStatus    // 에코딜 상품 여부
    ) {}

    public record DeliveryInfo(
            String recipientName,
            String recipientPhone,
            String zipCode,
            String address,
            String detailAddress,
            String deliveryMemo
    ) {}

    public record PickupInfo(
            Long departmentStoreId,
            String departmentStoreName,
            String pickupDate,
            String pickupMemo
    ) {}

}
