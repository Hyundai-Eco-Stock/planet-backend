package org.phoenix.planet.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.KafkaTopic;
import org.phoenix.planet.dto.car_access.raw.EcoCarEnterEvent;
import org.phoenix.planet.dto.offline.raw.KafkaOfflinePayInfo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfflineEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOfflinePayEvent(KafkaOfflinePayInfo kafkaOfflinePayInfo) {

        publish(KafkaTopic.OFFLINE_PAY_DETECTED, kafkaOfflinePayInfo);
    }

    public void publishEcoCarEnterEvent(EcoCarEnterEvent ecoCarEnterEvent) {

        publish(KafkaTopic.ECO_CAR_ENTER_DETECTED, ecoCarEnterEvent);
    }


    private void publish(KafkaTopic topic, Object payload) {
        // 안전성 체크
        if (!topic.getPayloadType().isAssignableFrom(payload.getClass())) {
            throw new IllegalArgumentException(
                "Invalid payload type for topic " + topic.getTopicName() +
                    ". Expected: " + topic.getPayloadType().getSimpleName() +
                    ", but got: " + payload.getClass().getSimpleName()
            );
        }

        kafkaTemplate.send(topic.getTopicName(), null, payload)
            .whenComplete((r, e) -> {
                if (e != null) {
                    log.error(e.getMessage(), e);
                }
            });
    }
}