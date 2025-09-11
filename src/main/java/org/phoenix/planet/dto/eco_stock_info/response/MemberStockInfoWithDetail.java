package org.phoenix.planet.dto.eco_stock_info.response;

public record MemberStockInfoWithDetail(
        Long memberStockInfoId,
        Long memberId,
        Long ecoStockId,
        Integer currentTotalQuantity,
        Double currentTotalAmount,
        Double point,
        String ecoStockName
) {
}
