package org.phoenix.planet.dto.eco_stock.raw;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockData {

    private Long stockPriceHistoryId;   // DB 시퀀스로 생성되는 PK
    private Long ecoStockId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stockTime;

    private Double stockPrice;
    private Integer sellCount;
    private Integer buyCount;
}
