package org.phoenix.planet.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PasswordResetTokenRepository {

    private final StringRedisTemplate redis;
    // TTL = 1시간
    private final Duration expiration = Duration.ofHours(1);
    private static final String redisKeyPrefix = "RESET_PW_TOKEN:";

    public void saveToken(String token, long memberId) {
        // key = RESET_PW_TOKEN:{token}
        // value = {memberId}

        String redisKey = redisKeyPrefix + token;
        redis.opsForValue()
            .set(redisKey, String.valueOf(memberId), expiration);
    }
}