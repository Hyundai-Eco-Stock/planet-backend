package org.phoenix.planet.dto.eco_stock.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcoStockWithLastPrice {

    private Long id;
    private String name;
    private Long quantity;
    private String imageUrl;
    private Double initPrice;
    private Double lastPrice;
    private Long stockPriceHistoryId;
}

