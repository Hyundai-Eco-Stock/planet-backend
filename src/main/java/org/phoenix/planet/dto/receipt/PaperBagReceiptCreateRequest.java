package org.phoenix.planet.dto.receipt;

import java.time.LocalDateTime;

public record PaperBagReceiptCreateRequest(
//    String eventId,
    Long memberId,
//    Integer totalAmount,
//    Integer itemCount,
    Boolean bagKeywordFound,
    LocalDateTime occurredAt
) {

    public PaperBagReceiptCreateRequest {

        occurredAt = LocalDateTime.now();
    }
}