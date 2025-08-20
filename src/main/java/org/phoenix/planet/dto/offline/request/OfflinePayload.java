package org.phoenix.planet.dto.offline.request;

import java.util.List;

public record OfflinePayload(
    int posId,
    long dailySeq,
    long shopId,
    long cardCompanyId,
    String cardNumber,
    int last4,
    List<Item> items,
    Summary summary
) {

    public record Item(
        long productId,
        int amount
    ) {

    }

    public record Summary(
        Integer subtotal,
        Integer discounts,
        Integer total
    ) {

    }
}