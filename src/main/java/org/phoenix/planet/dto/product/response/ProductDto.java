package org.phoenix.planet.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private Long productId;
    private String imageUrl;
    private Long categoryId;
    private Long brandId;
    private String name;
    private Integer price;
    private Integer quantity;
    private Boolean ecoDealStatus;
    private Integer salePercent;
    private String createdAt;
    private String updatedAt;
}
