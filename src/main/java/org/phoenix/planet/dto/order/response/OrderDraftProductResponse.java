package org.phoenix.planet.dto.order.response;

import lombok.Builder;
import org.phoenix.planet.constant.OrderType;
import org.phoenix.planet.dto.order.raw.PickupStoreInfo;

import java.util.List;

@Builder
public record OrderDraftProductResponse(
        Long productId,
        String productName,
        String productImageUrl,
        Long price,
        Integer quantity,
        OrderType orderType,
        List<PickupStoreInfo> availableStores  // 픽업 가능 매장들
) {
}
