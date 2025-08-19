package org.phoenix.planet.util.order;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 주문번호 생성
 * ORD + yyyyMMdd + 6자리 순번
 * 예시: ORD20240819000001, ORD20240819000002
 */
@Component
@RequiredArgsConstructor
public class OrderNumberGenerator {

    private final StringRedisTemplate redisTemplate;

    private static final String ORDER_NUMBER_KEY = "order:number:sequence";
    private static final String ORDER_PREFIX = "ORD";

    public String generateOrderNumber() {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = ORDER_NUMBER_KEY + today;

        Long sequence = redisTemplate.opsForValue().increment(key);

        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        Duration ttl = Duration.between(LocalDateTime.now(), tomorrow);

        redisTemplate.expire(key, ttl);

        String formattedSequence = String.format("%06d", sequence);

        return ORDER_PREFIX + today + formattedSequence;
    }

    public boolean isValidOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.length() != 17) {
            return false;
        }

        return orderNumber.startsWith(ORDER_PREFIX) &&
                orderNumber.substring(3, 11).matches("\\d{8}") &&
                orderNumber.substring(11).matches("\\d{6}");
    }

}
