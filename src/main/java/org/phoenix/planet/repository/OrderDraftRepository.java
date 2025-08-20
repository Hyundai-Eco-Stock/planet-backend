package org.phoenix.planet.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class OrderDraftRepository {

    private final StringRedisTemplate redisTemplate;

    private static final int TTL_MINUTES = 30;

    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(TTL_MINUTES));
    }

    public String findByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteByKey(String key) {
        redisTemplate.delete(key);
    }

    public boolean existsByKey(String key) {
        return redisTemplate.hasKey(key);
    }

}
