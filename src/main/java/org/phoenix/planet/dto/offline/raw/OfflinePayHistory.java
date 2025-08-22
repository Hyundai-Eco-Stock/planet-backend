package org.phoenix.planet.dto.offline.raw;

public record OfflinePayHistory(
    long offlinePayHistoryId,
    long shopId,
    long cardCompanyId,
    int cardNumberLast4,
    long totalPrice,
    String barcode,
    boolean stockIssued
) {

}
