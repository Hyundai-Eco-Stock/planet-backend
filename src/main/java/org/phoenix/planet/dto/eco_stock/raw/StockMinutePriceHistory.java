package org.phoenix.planet.dto.eco_stock.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMinutePriceHistory {

    private Long stockMinutePriceHistoryId;
    private Long ecoStockId;

    // 사람이 보기 좋은 시간 (분 단위, 초는 00으로)
    private LocalDateTime stockTimeMinute;

    // Epoch 초 (long 값)
    private Long stockTimeEpoch;

    private Double open;
    private Double high;
    private Double low;
    private Double close;

    private Integer value;
    private Integer sellCount;
    private Integer buyCount;
    private String color;
}
