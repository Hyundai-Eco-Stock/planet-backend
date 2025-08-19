package org.phoenix.planet.dto.department_store.response;

public record OfflineShopListResponse(
    Long offlineShopId,
    String shopName,
    String shopType,
    Long departmentStoreId,
    String departmentStoreName
) {

}