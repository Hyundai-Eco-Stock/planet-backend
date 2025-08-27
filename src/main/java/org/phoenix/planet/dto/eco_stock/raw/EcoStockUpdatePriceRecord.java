package org.phoenix.planet.dto.eco_stock.raw;

import lombok.Builder;

@Builder
public record EcoStockUpdatePriceRecord(
        Long ecoStockId,                           // 단일 값
        Integer stockIssueCount,
        Integer transactionHistoryCount,
        Long beforePrice,
        Integer quantity,
        Long initPrice
) {
}