package org.phoenix.planet.dto.pickup.raw;

public record OrderQrHeader(
    Long orderId,
    String orderNumber,
    Long storeId,
    String storeName,
    Long totalAmount,
    Long memberId,
    Long donationPrice
) {

}
