package org.phoenix.planet.configuration.kafka;

import org.apache.kafka.common.TopicPartition;
import org.phoenix.planet.constant.KafkaTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {

        DeadLetterPublishingRecoverer recoverer =
            new DeadLetterPublishingRecoverer(
                template,
                (cr, ex) -> new TopicPartition(KafkaTopic.DEAD_LETTER.getTopicName(),
                    cr.partition())
            );

        // 3회, 2초 간격 재시도 후 DLT
        DefaultErrorHandler handler =
            new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 3));

        // TODO: 필요 시 재시도 제외 예외 추가
        // handler.addNotRetryableExceptions(IllegalArgumentException.class);
        return handler;
    }
}