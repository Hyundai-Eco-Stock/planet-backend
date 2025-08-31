package org.phoenix.planet.service.order;

import org.phoenix.planet.dto.order.request.CreateOrderRequest;
import org.phoenix.planet.dto.order.response.CreateOrderResponse;
import org.phoenix.planet.dto.order.response.EcoStockIssueResponse;
import org.phoenix.planet.dto.order.response.OrderDraftResponse;

public interface OrderService {

    CreateOrderResponse createOrder(CreateOrderRequest request, Long memberId);

    OrderDraftResponse getOrderDraft(String orderNumber, Long memberId);

    EcoStockIssueResponse confirmPurchaseAndIssueEcoStock(Long orderHistoryId, Long memberId);

}
