package org.phoenix.planet.dto.order.response;

import org.phoenix.planet.constant.OrderStatus;
import org.phoenix.planet.dto.payment.response.PaymentResponse;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        Long orderHistoryId,
        String orderNumber,
        OrderStatus orderStatus,
        Long originPrice,
        Long usedPoint,
        Long donationPrice,
        Long finalPayPrice,
        String ecoDealQrUrl,
        Long departmentStoreId,
        String departmentStoreName,
        LocalDateTime createdAt,
        List<OrderProductResponse> orderProducts,
        PaymentResponse payment  // 결제 정보
) {
}
