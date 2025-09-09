package org.phoenix.planet.dto.eco_stock_info.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class EcoStockPriceResponse {

    private long ecoStockId;
    private long stockPriceHistoryId;
    private LocalDateTime stockTime;
    private Double stockPrice;
    private LocalDateTime createdAt;
}
