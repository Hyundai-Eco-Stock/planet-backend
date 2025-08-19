package org.phoenix.planet.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.constant.OrderError;
import org.phoenix.planet.dto.order.raw.OrderDraft;
import org.phoenix.planet.error.order.OrderException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderDraftRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String ORDER_DRAFT_PREFIX = "order:draft:";
    private static final int TTL_MINUTES = 30;

    public void save(OrderDraft orderDraft) {
        try {
            String key = ORDER_DRAFT_PREFIX + orderDraft.orderNumber();
            String value = objectMapper.writeValueAsString(orderDraft);

            redisTemplate.opsForValue().set(
                    key,
                    value,
                    Duration.ofMinutes(TTL_MINUTES)
            );
        } catch (JsonProcessingException e) {
            throw new OrderException(OrderError.ORDER_DRAFT_CREATION_FAILED);
        }
    }

    public Optional<OrderDraft> findByOrderNumber(String orderNumber) {
        try {
            String key = ORDER_DRAFT_PREFIX + orderNumber;
            String value = redisTemplate.opsForValue().get(key);

            if (value == null) {
                return Optional.empty();
            }

            OrderDraft orderDraft = objectMapper.readValue(value, OrderDraft.class);
            return Optional.of(orderDraft);
        } catch (JsonProcessingException e) {
            throw new OrderException(OrderError.INVALID_ORDER_DATA);
        }
    }

    public void deleteByOrderNumber(String orderNumber) {
        String key = ORDER_DRAFT_PREFIX + orderNumber;
        redisTemplate.delete(key);
    }

    public boolean existsByOrderNumber(String orderNumber) {
        String key = ORDER_DRAFT_PREFIX + orderNumber;
        return redisTemplate.hasKey(key);
    }

}
