package org.phoenix.planet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class OrderNumberRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String ORDER_NUMBER_KEY = "order:number:sequence";

    public Long getNextSequence(String dateKey) {
        String key = ORDER_NUMBER_KEY + dateKey;
        Long sequence = redisTemplate.opsForValue().increment(key);

        setExpirationToTomorrow(key);

        return sequence;
    }

    private void setExpirationToTomorrow(String key) {
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        Duration ttl = Duration.between(LocalDateTime.now(), tomorrow);
        redisTemplate.expire(key, ttl);
    }

}
