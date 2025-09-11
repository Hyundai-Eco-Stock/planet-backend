package org.phoenix.planet.dto.eco_stock_info.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellResponse {
    private Double executedPrice;      // 체결가
    private Integer currentTotalQuantity;  // 남은 주식 수량
    private Double currentTotalAmount;   // 남은 주식 총 금액
    private Double memberPoint;          // 업데이트된 멤버 포인트
}