package org.phoenix.planet.dto.member.response;

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
    private String ecoDealStatus;
    private String productName;
    private String productImageUrl;
    private String paymentStatus;
    private int salePercent;
    private int finalProductPrice;
}
