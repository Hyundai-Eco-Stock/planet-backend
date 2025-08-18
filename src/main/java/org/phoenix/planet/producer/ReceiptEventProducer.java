package org.phoenix.planet.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, String key, Object payload) {

        kafkaTemplate.send(topic, key, payload)
            .whenComplete((r, e) -> {
                if (e != null) {
                    log.error(e.getMessage(), e);
                }
            });
    }
}