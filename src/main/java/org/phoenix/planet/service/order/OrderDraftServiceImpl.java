package org.phoenix.planet.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.constant.OrderError;
import org.phoenix.planet.dto.order.raw.OrderDraft;
import org.phoenix.planet.error.order.OrderException;
import org.phoenix.planet.repository.OrderDraftRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDraftServiceImpl implements OrderDraftService {

    private final OrderDraftRepository orderDraftRepository;
    private final ObjectMapper objectMapper;

    private static final String ORDER_DRAFT_PREFIX = "order:draft:";

    @Override
    public void saveOrderDraft(OrderDraft orderDraft) {
        try {
            String key = ORDER_DRAFT_PREFIX + orderDraft.orderNumber();
            String value = objectMapper.writeValueAsString(orderDraft);
            orderDraftRepository.save(key, value);
        } catch (JsonProcessingException e) {
            throw new OrderException(OrderError.ORDER_DRAFT_CREATION_FAILED);
        }
    }

    @Override
    public Optional<OrderDraft> findByOrderNumber(String orderNumber) {
        try {
            String key = ORDER_DRAFT_PREFIX + orderNumber;
            String value = orderDraftRepository.findByKey(key);

            if (value == null) {
                return Optional.empty();
            }

            OrderDraft orderDraft = objectMapper.readValue(value, OrderDraft.class);
            return Optional.of(orderDraft);
        } catch (JsonProcessingException e) {
            throw new OrderException(OrderError.INVALID_ORDER_DATA);
        }
    }

    @Override
    public void deleteByOrderNumber(String orderNumber) {
        String key = ORDER_DRAFT_PREFIX + orderNumber;
        orderDraftRepository.deleteByKey(key);
    }

    @Override
    public boolean existsByOrderNumber(String orderNumber) {
        String key = ORDER_DRAFT_PREFIX + orderNumber;
        return orderDraftRepository.existsByKey(key);
    }

}
