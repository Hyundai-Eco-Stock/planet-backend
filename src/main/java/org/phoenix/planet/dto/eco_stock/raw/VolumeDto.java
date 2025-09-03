package org.phoenix.planet.dto.eco_stock.raw;

public record VolumeDto(
        long stockPriceHistoryId,
        long time,
        int value,
        String color,
        int sellCount,
        int buyCount
) {
}
