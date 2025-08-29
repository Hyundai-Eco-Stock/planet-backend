package org.phoenix.planet.dto.product.request;

import jakarta.validation.constraints.NotBlank;


public record RecommendRequest(
        @NotBlank String name,
        String categoryId,
        String productId,
        Integer size // 추천 사이즈
) {

}
