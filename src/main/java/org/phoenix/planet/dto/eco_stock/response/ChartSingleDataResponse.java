package org.phoenix.planet.dto.eco_stock.response;

import lombok.Builder;
import org.phoenix.planet.dto.eco_stock.raw.OhlcDto;
import org.phoenix.planet.dto.eco_stock.raw.VolumeDto;

@Builder
public record ChartSingleDataResponse(
        Long ecoStockId,
        OhlcDto ohlcData,
        VolumeDto volumeData
) {
}
