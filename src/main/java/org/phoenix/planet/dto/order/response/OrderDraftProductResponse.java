package org.phoenix.planet.dto.order.response;

import java.util.List;
import lombok.Builder;
import org.phoenix.planet.constant.order.OrderType;
import org.phoenix.planet.dto.order.raw.PickupStoreInfo;

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
