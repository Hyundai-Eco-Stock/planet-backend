package org.phoenix.planet.dto.eco_stock_info.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcoStockPriceResponse {

    private long ecoStockId;
    private long stockPriceHistoryId;
    private LocalDateTime stockTime;
    private int stockPrice;
    private LocalDateTime createdAt;
}
