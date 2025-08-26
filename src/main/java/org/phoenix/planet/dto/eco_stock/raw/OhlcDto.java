package org.phoenix.planet.dto.eco_stock.raw;

public record OhlcDto(
        long stockPriceHistoryId,
        long time,
        long open,
        long high,
        long low,
        long close,
        boolean isEmpty
) {
}