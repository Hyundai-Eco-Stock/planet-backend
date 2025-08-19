package org.phoenix.planet.dto.eco_stock.raw;

public record EcoStock(
    Long id,
    String name,
    Long quantity,
    String imageUrl
) {

}
