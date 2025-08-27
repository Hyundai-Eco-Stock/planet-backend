package org.phoenix.planet.dto.order.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyOrderResponse {

    private Long orderHistoryId;
    private String orderNumber;
    private String orderStatus;
    private int originPrice;
    private int usedPoint;
    private int donationPrice;
    private int finalPayPrice;
    private String ecoDealQrUrl;
    private Long memberId;
    private String createdAt;
    private Long orderProductId;
    private int price;
    private int quantity;
    private String cancelStatus;
    private String orderType;
    private Long productId;
    private int discountPrice;
    private String ecoDealStatus;
    private String productName;
    private String productImageUrl;
}
