package org.phoenix.planet.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EcoProductListResponse {

    private long productId;

    private Long categoryId;

    private String categoryName;

    private Long brandId;

    private String name;

    private int price;

    private int quantity;

    private int salePercent;
}
