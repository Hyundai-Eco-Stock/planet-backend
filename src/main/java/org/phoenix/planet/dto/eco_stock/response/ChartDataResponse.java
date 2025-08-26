package org.phoenix.planet.dto.eco_stock.response;

import lombok.Builder;
import org.phoenix.planet.dto.eco_stock.raw.OhlcDto;
import org.phoenix.planet.dto.eco_stock.raw.VolumeDto;

import java.util.List;

@Builder
public record ChartDataResponse(
        List<OhlcDto> ohlcData,
        List<VolumeDto> volumeData
) {
}
