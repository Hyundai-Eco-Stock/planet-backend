package org.phoenix.planet.dto.offline_pay.request;

import java.util.List;

public record OfflinePayload(
    Long storeId,
    Long cardCompanyId,
    String cardNumber,
    String last4,
    List<Item> items,
    Summary summary
) {

    public record Item(
        Object productId, // 숫자 or 문자열 모두 가능하다면 Object,
        // 만약 숫자/문자 혼용이 아니라면 Long or String으로 확정하는 게 좋음
        String name,
        Integer price
    ) {

    }

    public record Summary(
        Integer subtotal,
        Integer discounts,
        Integer total
    ) {

    }
}