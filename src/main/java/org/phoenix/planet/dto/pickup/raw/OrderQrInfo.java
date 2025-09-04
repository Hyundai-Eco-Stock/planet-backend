package org.phoenix.planet.dto.pickup.raw;

import java.util.List;

public record OrderQrInfo(
    Long orderId,
    String orderNumber,
    Long storeId,
    String storeName,
    List<ProductQrInfo> products,
    Long totalAmount
) {

}
