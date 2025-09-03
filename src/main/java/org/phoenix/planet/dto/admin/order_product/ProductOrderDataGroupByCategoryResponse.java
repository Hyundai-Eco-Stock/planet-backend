package org.phoenix.planet.dto.admin.order_product;

import java.util.List;
import lombok.Builder;

@Builder
public record ProductOrderDataGroupByCategoryResponse(
    String topCategory,
    List<CategoryItem> items
) {

}
