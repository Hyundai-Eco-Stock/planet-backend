package org.phoenix.planet.dto.product.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailResponse {

    private Long productId;
    private String productName;
    private Long price;
    private String imageUrl;
    private Long categoryId;
    private Long brandId;
    private Long productImageId;
    private String productImageUrl;
    private String brandName;
    private int sortOrder;
}
