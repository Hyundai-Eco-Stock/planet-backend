package org.phoenix.planet.service.phti;

import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.configuration.phti.PhtiApiProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PhtiApiKeyManager {

    private final List<String> apiKeys;
    private final StringRedisTemplate redisTemplate;
    private static final String LOCK_KEY = "phti:api-key:lock";
    private static final String INDEX_KEY = "phti:api-key:index";
    private static final long LOCK_TIMEOUT_SECONDS = 20;
    private static final long WAIT_TIME_MILLIS = 100;
    private static final int MAX_RETRIES = 50; // 50 * 100ms = 5 seconds

    public PhtiApiKeyManager(
        PhtiApiProperties phtiApiProperties,
        StringRedisTemplate redisTemplate) {

        this.apiKeys = phtiApiProperties.getApiKeys();
        this.redisTemplate = redisTemplate;

        log.info("✅ PhtiApiKeyManager 초기화 완료. API 키 개수: {}",
            (apiKeys != null ? apiKeys.size() : 0));
    }

    public String getNextKey() {

        int retries = 0;

        // 1. 락 획득 시도
        while (!acquireLock()) {
            retries++;
            if (retries > MAX_RETRIES) {
                log.error("❌ PHTI API 키 락 획득 실패 ({}회 시도)", retries);
                throw new RuntimeException("PHTI API 키 락 획득에 실패했습니다.");
            }
            try {
                Thread.sleep(WAIT_TIME_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("락 대기 중 스레드 인터럽트 발생", e);
            }
        }

        try {
            // 2. 락 획득 성공 시, 인덱스를 가져와서 1 증가 (라운드 로빈)
            Long index = redisTemplate.opsForValue().increment(INDEX_KEY);
            if (index == null) {
                log.error("❌ Redis에서 PHTI API 키 인덱스를 가져올 수 없음");
                throw new IllegalStateException("Redis에서 PHTI API 키 인덱스를 가져올 수 없습니다.");
            }

            int keyIndex = (int) (index % apiKeys.size());
            String selectedKey = apiKeys.get(keyIndex);

            log.info("🔑 API 키 선택됨: index={} (Redis값: {}), keyPrefix={}",
                keyIndex, index, maskKey(selectedKey));

            return selectedKey;
        } finally {
            // 3. 작업 완료 후 락 해제
            releaseLock();
        }
    }

    private boolean acquireLock() {

        Boolean success = redisTemplate.opsForValue()
            .setIfAbsent(LOCK_KEY, "locked", Duration.ofSeconds(LOCK_TIMEOUT_SECONDS));

        log.debug("🔒 락 획득 시도: {}", success);
        return Boolean.TRUE.equals(success);
    }

    private void releaseLock() {

        redisTemplate.delete(LOCK_KEY);
        log.debug("🔓 락 해제 완료");
    }

    /**
     * 키 전체를 노출하지 않도록 앞 6자리만 보여줌
     */
    private String maskKey(String key) {

        if (key == null || key.length() < 6) {
            return "UNKNOWN";
        }
        return key.substring(0, 20) + "******";
    }
}