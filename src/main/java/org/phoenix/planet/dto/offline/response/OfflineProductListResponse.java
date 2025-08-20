package org.phoenix.planet.dto.offline.response;

public record OfflineProductListResponse(
    Long productId,
    Long shopId,
    String name,
    int price
) {

}
