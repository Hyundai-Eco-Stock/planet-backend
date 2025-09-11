package org.phoenix.planet.dto.eco_stock.raw;

public record OhlcDto(
        long stockPriceHistoryId,
        long time,
        double open,
        double high,
        double low,
        double close,
        boolean isEmpty
) {
}