package org.phoenix.planet.dto.order.response;

import java.time.LocalDateTime;
import java.util.List;
import org.phoenix.planet.constant.order.OrderStatus;
import org.phoenix.planet.dto.payment.response.PaymentConfirmResponse;

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
    PaymentConfirmResponse payment  // 결제 정보
) {

}
