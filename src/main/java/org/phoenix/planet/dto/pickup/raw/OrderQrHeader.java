package org.phoenix.planet.dto.pickup.raw;

import org.phoenix.planet.constant.OrderStatus;

public record OrderQrHeader(
    Long orderId,
    String orderNumber,
    Long storeId,
    String storeName,
    Long totalAmount,
    Long memberId,
    Long donationPrice,
    OrderStatus orderStatus
) {

}
