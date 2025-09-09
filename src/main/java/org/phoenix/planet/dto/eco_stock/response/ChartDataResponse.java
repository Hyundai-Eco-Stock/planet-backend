package org.phoenix.planet.dto.eco_stock.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.phoenix.planet.dto.eco_stock.raw.OhlcDto;
import org.phoenix.planet.dto.eco_stock.raw.VolumeDto;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartDataResponse{
    private List<OhlcDto> ohlcData;
    private List<VolumeDto> volumeData;
    private OhlcDto lastOhlcData;
    private VolumeDto lastVolumeData;
}
