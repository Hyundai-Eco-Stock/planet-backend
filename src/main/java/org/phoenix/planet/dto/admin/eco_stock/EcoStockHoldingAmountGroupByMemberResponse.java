package org.phoenix.planet.dto.admin.eco_stock;

import java.util.List;
import lombok.Builder;

@Builder
public record EcoStockHoldingAmountGroupByMemberResponse(
    long totalUsers,              // 총 사용자 수
    long totalIssued,             // 총 발급량
    double avgHolding,            // 평균 보유량
    List<HoldingItem> items       // 구간별 분포
) {

}