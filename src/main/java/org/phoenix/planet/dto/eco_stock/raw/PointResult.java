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
public class PointResult {
    private Integer success;
    private String message;
    private LocalDateTime transactionTime;
    private Integer currentTotalQuantity;
    private Double currentTotalAmount;
    private Double memberPoint;
}