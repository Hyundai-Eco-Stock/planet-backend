package org.phoenix.planet.dto.eco_stock.raw;

public record MemberStockInfo(

        Long memberStockInfoId,
        Long memberId,
        Long ecoStockId,
        Integer currentTotalQuantity,
        Long currentTotalAmount,
        Integer point
) {
}