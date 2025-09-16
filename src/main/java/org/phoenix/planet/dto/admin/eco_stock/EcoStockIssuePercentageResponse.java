package org.phoenix.planet.dto.admin.eco_stock;

import java.util.List;
import lombok.Builder;

@Builder
public record EcoStockIssuePercentageResponse(
    long totalIssued,             // 총 발급량
//    double avgHolding,            // 평균 보유량
    int ecoStockTypes,            // 에코스톡 종류 수
    List<IssueItem> items         // 각 에코스톡별 비율 데이터
) {

}