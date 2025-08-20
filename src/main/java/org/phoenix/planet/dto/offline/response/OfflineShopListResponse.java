package org.phoenix.planet.dto.offline.response;

public record OfflineShopListResponse(
    Long offlineShopId,
    String shopName,
    String shopType,
    Long departmentStoreId,
    String departmentStoreName
) {

}