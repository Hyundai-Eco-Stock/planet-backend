package org.phoenix.planet.dto.payment.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import org.phoenix.planet.constant.order.OrderType;

public record PaymentConfirmRequest(
    // TossPayments 필수 파라미터
    @NotBlank(message = "결제키는 필수입니다")
    String paymentKey,

    @NotBlank(message = "주문 ID는 필수입니다")
    String orderId,

    @NotNull(message = "결제 금액은 필수입니다")
    @Positive(message = "결제 금액은 0보다 커야 합니다")
    Integer amount,

    @NotBlank(message = "주문명은 필수입니다")
    String orderName,

    // 고객 정보
    String customerName,

    @Email(message = "이메일 형식이 올바르지 않습니다")
    String customerEmail,

    String customerPhone,

    // 주문 상품 정보
    @NotEmpty(message = "상품 목록은 비어 있을 수 없습니다")
    List<OrderProductInfo> products,

    // 배송 정보 (일반 배송인 경우)
    DeliveryInfo deliveryInfo,

    // 픽업 정보 (픽업 배송인 경우)
    PickupInfo pickupInfo,

    // 결제 옵션
    @PositiveOrZero(message = "사용 포인트는 0 이상이어야 합니다")
    Integer pointsUsed,      // 사용 포인트

    @PositiveOrZero(message = "기부 금액은 0 이상이어야 합니다")
    Integer donationAmount,  // 기부 금액

    @NotNull(message = "주문 타입은 필수입니다")
    OrderType orderType
) {

    public record OrderProductInfo(
        @NotNull(message = "상품 ID는 필수입니다")
        Long productId,

        String productName,

        @NotNull(message = "상품 가격은 필수입니다")
        @Positive(message = "상품 가격은 0보다 커야 합니다")
        Integer price,           // 원가

        @Min(value = 0, message = "할인율은 0~100 사이여야 합니다")
        @Max(value = 100, message = "할인율은 0~100 사이여야 합니다")
        Integer salePercent,     // 할인율 (0-100)

        @NotNull(message = "수량은 필수입니다")
        @Positive(message = "수량은 0보다 커야 합니다")
        Integer quantity,

        @NotNull(message = "에코딜 여부는 필수입니다")
        Boolean ecoDealStatus    // 에코딜 상품 여부
    ) {

    }

    public record DeliveryInfo(
        @NotBlank(message = "수령인 이름은 필수입니다.")
        String recipientName,

        @NotBlank(message = "수령인 연락처는 필수입니다.")
        String recipientPhone,

        @NotBlank(message = "우편번호는 필수입니다.")
        String zipCode,

        @NotBlank(message = "주소는 필수입니다.")
        String address,

        String detailAddress,
        String deliveryMemo
    ) {

    }

    public record PickupInfo(
        @NotNull(message = "백화점 ID는 필수입니다.")
        Long departmentStoreId,

        @NotBlank(message = "백화점 이름은 필수입니다.")
        String departmentStoreName,

        @NotBlank(message = "픽업 날짜는 필수입니다.")
        String pickupDate,

        String pickupMemo
    ) {

    }

}
