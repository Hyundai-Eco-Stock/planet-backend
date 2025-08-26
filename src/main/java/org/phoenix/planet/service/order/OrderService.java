package org.phoenix.planet.service.order;

import java.util.List;
import org.phoenix.planet.dto.order.request.CreateOrderRequest;
import org.phoenix.planet.dto.order.response.CreateOrderResponse;
import org.phoenix.planet.dto.order.response.MyOrderResponse;
import org.phoenix.planet.dto.order.response.OrderDraftResponse;

public interface OrderService {

    CreateOrderResponse createOrder(CreateOrderRequest request, Long memberId);

    OrderDraftResponse getOrderDraft(String orderNumber, Long memberId);

    List<MyOrderResponse> getMyOrders(Long memberId);
}
