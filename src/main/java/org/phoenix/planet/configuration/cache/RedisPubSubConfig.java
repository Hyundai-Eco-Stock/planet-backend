package org.phoenix.planet.configuration.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.service.websocket.StockDataSubscriber;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisPubSubConfig {

    @Value("${custom-redis.host}")
    private String secondHost;

    @Value("${custom-redis.port}")
    private int secondPort;

    @PostConstruct
    public void logRedisConfig() {
        log.info("🔧 WebSocket Redis 설정 - Host: {}, Port: {}", secondHost, secondPort);
    }

    @Bean(name = "webSocketRedisConnectionFactory")
    public LettuceConnectionFactory webSocketRedisConnectionFactory() {
        log.info("🔧 WebSocket Redis ConnectionFactory 생성 시작");

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(secondHost, secondPort);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);

        // 연결 검증 활성화
        factory.setValidateConnection(true);

        // 필수! 팩토리 초기화
        factory.afterPropertiesSet();

        log.info("✅ WebSocket Redis ConnectionFactory 생성 완료");
        return factory;
    }

    @Bean(name = "webSocketRedisTemplate")
    public StringRedisTemplate webSocketRedisTemplate(
            @Qualifier("webSocketRedisConnectionFactory") LettuceConnectionFactory connectionFactory
    ) {
        log.info("🔧 WebSocket Redis Template 생성 시작");

        StringRedisTemplate template = new StringRedisTemplate(connectionFactory);

        // 연결 테스트
        try {
            Objects.requireNonNull(template.getConnectionFactory()).getConnection().ping();
            log.info("✅ WebSocket Redis 연결 테스트 성공!");
        } catch (Exception e) {
            log.error("❌ WebSocket Redis 연결 테스트 실패: {}", e.getMessage());
        }

        return template;
    }

    @Bean(name = "webSocketMessageListenerContainer")
    public RedisMessageListenerContainer webSocketMessageListenerContainer(
            @Qualifier("webSocketRedisConnectionFactory") LettuceConnectionFactory connectionFactory,
            StockDataSubscriber subscriber
    ) {
        log.info("🔧 WebSocket Redis Message Listener Container 생성 시작");

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // MessageListenerAdapter 없이 직접 등록
        container.addMessageListener(subscriber, new PatternTopic("stock-channel"));

        log.info("🔧 Redis 채널 'stock-channel' 구독 설정 완료!");
        log.info("🔧 Subscriber: {}", subscriber.getClass().getSimpleName());

        return container;
    }
}