package org.phoenix.planet.configuration.kafka;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.KafkaTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTopicInitializer {

    @Value("${kafka.topic.prefix}")
    private String prefix;

    @PostConstruct
    public void init() {

        log.info("[KafkaTopicInitializer] prefix: {}", prefix);
        KafkaTopic.init(prefix);
    }
}