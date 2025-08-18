package org.phoenix.planet.dto.order.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.phoenix.planet.constant.CancelStatus;
import org.phoenix.planet.constant.OrderType;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProduct {

    private Long orderProductId;

    private Long price;

    private Integer quantity;

    private CancelStatus cancelStatus;  // 취소 상태 (Y: 취소됨, N: 정상)

    private OrderType orderType;  // 주문 타입 (PICKUP: 픽업, DELIVERY: 배송)

    private Long ecoDealDiscount;  // 에코딜 할인 금액 (주문 시점 할인 내역 보존용)

    private Long orderHistoryId;

    private Long productId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
