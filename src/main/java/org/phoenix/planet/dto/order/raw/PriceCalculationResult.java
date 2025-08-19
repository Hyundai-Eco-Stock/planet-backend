package org.phoenix.planet.dto.order.raw;

import java.util.Map;

public record PriceCalculationResult(
        Long originPrice,
        Long totalEcoDealDiscount,
        Long usedPoint,
        Long donationPrice,
        Long finalPayPrice,
        Map<Long, Long> ecoDealDiscountMap  // 상품별 에코딜 할인 금액
) {

    public Long getEcoDealDiscountForProduct(Long productId) {
        return ecoDealDiscountMap.getOrDefault(productId, 0L);
    }

}
