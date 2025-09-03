package org.phoenix.planet.dto.admin.eco_stock;

public record HoldingItem(
    String range,             // "1-10개" 같은 구간
    long userCount,
    double percentage
) {

}