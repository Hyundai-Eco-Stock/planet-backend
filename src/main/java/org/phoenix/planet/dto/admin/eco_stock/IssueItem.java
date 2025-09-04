package org.phoenix.planet.dto.admin.eco_stock;

public record IssueItem(
    String name,
    int count,
    double value,   // 비율 (%)
    String color
) {

}