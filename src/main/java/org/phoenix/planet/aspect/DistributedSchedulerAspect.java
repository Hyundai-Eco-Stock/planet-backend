package org.phoenix.planet.aspect;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.phoenix.planet.annotation.DistributedScheduled;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DistributedSchedulerAspect {

    private final StringRedisTemplate redisTemplate;

    private static final String ACQUIRE_LOCK_SCRIPT = """
        if redis.call('exists', KEYS[1]) == 0 then
            redis.call('set', KEYS[1], ARGV[1])
            redis.call('pexpire', KEYS[1], ARGV[2])
            return 1
        else
            return 0
        end
        """;

    private static final String RELEASE_LOCK_SCRIPT = """
        if redis.call("get", KEYS[1]) == ARGV[1] then
            return redis.call("del", KEYS[1])
        else
            return 0
        end
        """;

    public DistributedSchedulerAspect(
        @Qualifier("webSocketRedisTemplate") StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(dist)")
    public Object lockAndRun(ProceedingJoinPoint joinPoint, DistributedScheduled dist)
        throws Throwable {

        String lockKey = dist.lockKey();
        String lockId = UUID.randomUUID().toString(); // 🔑 고유 락 ID 생성

        long expireMillis = Duration.ofSeconds(dist.expireSeconds()).toMillis();

        Long acquired = redisTemplate.execute(
            new DefaultRedisScript<>(ACQUIRE_LOCK_SCRIPT, Long.class),
            Collections.singletonList(lockKey),
            lockId,
            String.valueOf(expireMillis)
        );

        if (Long.valueOf(1).equals(acquired)) {
            log.debug("[LOCK ACQUIRED] {}", lockKey);
            try {
                return joinPoint.proceed();
            } finally {
                redisTemplate.execute(
                    new DefaultRedisScript<>(RELEASE_LOCK_SCRIPT, Long.class),
                    Collections.singletonList(lockKey),
                    lockId
                );
                log.debug("[LOCK RELEASED] {}", lockKey);
            }
        } else {
            log.info("[LOCK SKIPPED] Already locked: {}", lockKey);
            return null;
        }
    }
}
