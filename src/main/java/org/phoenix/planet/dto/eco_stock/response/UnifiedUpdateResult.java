package org.phoenix.planet.dto.eco_stock.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.phoenix.planet.dto.eco_stock.raw.OhlcDto;
import org.phoenix.planet.dto.eco_stock.raw.VolumeDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnifiedUpdateResult {

    private Double executedPrice;
    private Double newMarketPrice;
    private Integer newQuantity;

    private OhlcDto ohlcDto;
    private VolumeDto volumeDto;
    private Long historyId;
    private LocalDateTime transactionTime;
}
