package org.phoenix.planet.dto.order.response;

import lombok.Builder;
import org.phoenix.planet.dto.order.raw.PickupStoreInfo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문서 조회 응답 DTO
 * 주문서 페이지에 표시할 모든 정보
 */
@Builder
public record OrderDraftResponse(
        String orderNumber,
        Long totalAmount,
        Long usedPoint,
        Long donationPrice,
        List<OrderDraftProductResponse> products,

        // DELIVERY용 필드
        String deliveryAddress,
        String recipientName,

        // PICKUP용 필드
        Long selectedPickupStoreId,    // 사용자가 선택한 매장 ID
        String selectedPickupStoreName,  // 선택한 매장명
        List<PickupStoreInfo> availablePickupStores,  // 선택 가능한 매장들

        LocalDateTime createdAt,
        LocalDateTime validUntil
) {
}
