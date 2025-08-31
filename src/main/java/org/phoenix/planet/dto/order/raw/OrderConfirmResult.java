package org.phoenix.planet.dto.order.raw;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderConfirmResult(
        Long orderHistoryId,
        String orderNumber,
        Long donationPrice,
        LocalDateTime confirmedAt
) {
}
