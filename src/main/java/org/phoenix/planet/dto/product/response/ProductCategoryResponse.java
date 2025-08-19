package org.phoenix.planet.dto.product.response;

import java.util.List;
import lombok.Builder;

@Builder
public record ProductCategoryResponse(
        List<ProductDto> products,
        List<ProductCategoryDto> categories
) {

}
