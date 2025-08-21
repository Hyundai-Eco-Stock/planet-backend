package org.phoenix.planet.dto.order.raw;

public record PickupStoreProductInfo(
        Long productId,
        Long storeId,
        String storeName
) {
}
