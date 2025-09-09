package org.phoenix.planet.dto.eco_stock.raw;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisStockPriceHistory {
    private Long stockPriceHistoryId;
    private Long ecoStockId;
    private LocalDateTime time;  // epoch timestamp
    private Double price;
    private Long quantity;
    private Integer tradeQuantity;
    private String tradeType; // "SELL", "BUY", "SAME"
    private Integer sellCount;
    private Integer buyCount;
}