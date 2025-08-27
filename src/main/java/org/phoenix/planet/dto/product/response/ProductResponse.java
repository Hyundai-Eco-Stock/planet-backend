package org.phoenix.planet.dto.product.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ProductResponse {

    private Long productId;
    private String imageUrl;
    private Long categoryId;
    private Long brandId;
    private String productName;
    private Long price;
    private Integer quantity;
    private String ecoDealStatus;
    private Integer salePercent;
    private String brandName;
}
