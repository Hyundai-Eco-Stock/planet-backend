package org.phoenix.planet.dto.order.raw;

import java.util.List;

public record OrderValidationResult(
        List<OrderValidationProduct> products,
        Long totalAmount
) {
}
