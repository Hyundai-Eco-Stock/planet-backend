package org.phoenix.planet.dto.product.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcoProductDetailResponse {

    private long productId;
    private String productName;
    private int price;
    private int quantity;
    private int salePercent;
    private String imageUrl;
    private long departmentStoreId;
    private String departmentStoreName;
    private double lat;
    private double lng;
}
