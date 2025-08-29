package org.phoenix.planet.dto.order.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EcoStockIssueResponse(
        Long orderHistoryId,
        String orderNumber,
        Long memberId,
        int issuedStockCount,
        int issuedStockValue,
        int totalPurchaseAmount,   // 총 매입금액
        int totalStockCount,    // 총 보유 스톡 수량
        LocalDateTime confirmedAt,
        String message
) {
}
