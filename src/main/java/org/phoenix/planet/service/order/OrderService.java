package org.phoenix.planet.service.order;

import org.phoenix.planet.dto.order.raw.OrderConfirmResult;
import org.phoenix.planet.dto.order.request.CreateOrderRequest;
import org.phoenix.planet.dto.order.response.CreateOrderResponse;
import org.phoenix.planet.dto.order.response.OrderDraftResponse;

public interface OrderService {

    CreateOrderResponse createOrder(CreateOrderRequest request, Long memberId);

    OrderDraftResponse getOrderDraft(String orderNumber, Long memberId);

    OrderConfirmResult confirmPurchase(Long orderHistoryId, Long memberId);

}
