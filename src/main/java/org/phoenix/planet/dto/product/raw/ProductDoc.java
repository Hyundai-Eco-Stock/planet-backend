package org.phoenix.planet.dto.product.raw;

public record ProductDoc(
    String productId,
    String productName,
    String brandName,
    String categoryName,
    String categoryId,
    String imageUrl
) {

}