package org.phoenix.planet.dto.order.raw;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.phoenix.planet.constant.order.OrderStatus;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistory {

    private Long orderHistoryId;

    private String orderNumber;  // 주문번호 (TossPayments orderId로 사용)

    private OrderStatus orderStatus;

    private Long originPrice;

    @Builder.Default
    private Long usedPoint = 0L;

    @Builder.Default
    private Long donationPrice = 0L;

    private Long finalPayPrice;  // 최종 결제 금액 = originPrice - usedPoint + donationPrice

    private String ecoDealQrUrl;

    private Long memberId;

    private Long departmentStoreId;    // FK: 백화점 ID (에코딜 주문 시 사용)

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder.Default
    private Long refundDonationPrice = 0L;

}
