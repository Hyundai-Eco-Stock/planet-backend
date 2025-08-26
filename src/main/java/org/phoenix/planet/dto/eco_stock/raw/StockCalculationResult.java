package org.phoenix.planet.dto.eco_stock.raw;

import lombok.Builder;

@Builder
public record StockCalculationResult(
        StockData stockData,
        int updateQuantity
) {
}