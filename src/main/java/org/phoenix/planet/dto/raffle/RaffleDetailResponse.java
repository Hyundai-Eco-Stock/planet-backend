package org.phoenix.planet.dto.raffle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaffleDetailResponse {
    private Long raffleId;
    private Integer ecoStockAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long productId;
    private String productName;
    private Integer price;
    private String imageUrl;
    private String brandName;
    private Long ecoStockId;
    private String ecoStockName;
    private List<ProductImage> images;

}