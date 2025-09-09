package org.phoenix.planet.dto.eco_stock.request;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellStockRequest {
    // IN 파라미터
    private Long ecoStockId;
    private Double sellPrice;
    private Integer sellCount;

    // OUT 파라미터를 받을 필드 (프로시저의 OUT 파라미터명과 일치시킬 필요는 없음)
    private Integer pSuccess; // NUMERIC -> Integer 또는 int
    private String pMessage;  // VARCHAR -> String
    private Double newPrice;
    private LocalDateTime transactionTime;
}