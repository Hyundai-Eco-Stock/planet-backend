package org.phoenix.planet.service.order;

import org.phoenix.planet.dto.order.raw.OrderDraft;

import java.util.Optional;

public interface OrderDraftService {

    void saveOrderDraft(OrderDraft orderDraft);

    Optional<OrderDraft> findByOrderNumber(String orderNumber);

    void deleteByOrderNumber(String orderNumber);

    boolean existsByOrderNumber(String orderNumber);

}
