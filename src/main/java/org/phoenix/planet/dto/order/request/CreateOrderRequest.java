package org.phoenix.planet.dto.order.request;

import static org.phoenix.planet.constant.error.OrderError.MIXED_ORDER_TYPE;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.phoenix.planet.constant.order.OrderType;
import org.phoenix.planet.error.order.OrderException;

/**
 * 주문 생성 요청 DTO 픽업과 일반배송은 동시 주문 불가능 (한 주문 내 모든 상품은 동일한 타입)
 */
public record CreateOrderRequest(
    @NotEmpty(message = "주문할 상품이 없습니다") @Valid List<OrderProductRequest> products,  // 주문할 상품 목록
    @Min(value = 0, message = "사용 포인트는 0 이상이어야 합니다") Long usedPoint,
    @Min(value = 0, message = "기부 금액은 0 이상이어야 합니다") Long donationPrice
) {

    public CreateOrderRequest {

        validateOrderTypes(products);
    }

    private void validateOrderTypes(List<OrderProductRequest> products) {
        // 첫 번째 상품의 주문 타입을 기준으로 설정
        OrderType firstOrderType = products.getFirst().orderType();
        boolean allSameType = products.stream()
            .allMatch(product -> product.orderType() == firstOrderType);

        if (!allSameType) {
            throw new OrderException(MIXED_ORDER_TYPE);
        }
    }

}
