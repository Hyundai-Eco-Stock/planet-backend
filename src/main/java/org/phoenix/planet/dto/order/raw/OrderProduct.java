package org.phoenix.planet.dto.order.raw;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.phoenix.planet.constant.order.CancelStatus;
import org.phoenix.planet.constant.order.OrderType;

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

    private Long discountPrice;

    private Long orderHistoryId;

    private Long productId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
