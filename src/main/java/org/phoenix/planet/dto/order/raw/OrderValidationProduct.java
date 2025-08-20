package org.phoenix.planet.dto.order.raw;

public record OrderValidationProduct(
        Long productId,
        Integer quantity,
        Long unitPrice,
        Long totalPrice,
        String productName
) {
}
