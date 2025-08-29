package org.phoenix.planet.dto.member.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyEcoDealResponse {

    private Long orderHistoryId;
    private String orderNumber;
    private String orderStatus;
    private Integer originPrice;
    private Integer usedPoint;
    private Integer donationPrice;
    private Integer finalPayPrice;
    private String ecoDealQrUrl;
    private String createdAt;
    private Long productId;
    private Integer price;
    private Integer quantity;
    private String cancelStatus;
    private String orderType;
    private String productName;
    private String ecoDealStatus;
    private Integer salePercent;
    private String imageUrl;
}
