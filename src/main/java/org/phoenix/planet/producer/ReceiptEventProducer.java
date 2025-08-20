package org.phoenix.planet.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.KafkaTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(KafkaTopic topic, Object payload) {
        // 안전성 체크
        if (!topic.getPayloadType().isAssignableFrom(payload.getClass())) {
            throw new IllegalArgumentException(
                "Invalid payload type for topic " + topic.getValue() +
                    ". Expected: " + topic.getPayloadType().getSimpleName() +
                    ", but got: " + payload.getClass().getSimpleName()
            );
        }

        kafkaTemplate.send(topic.getValue(), null, payload)
            .whenComplete((r, e) -> {
                if (e != null) {
                    log.error(e.getMessage(), e);
                }
            });
    }
}