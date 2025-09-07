package org.phoenix.planet.configuration.kafka;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.phoenix.planet.constant.kafka.KafkaTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class KafkaTopicConfig {

    @Bean
    public List<NewTopic> topics() {

        return List.of(
            // 오프라인 결제 내역 감지
            new NewTopic(KafkaTopic.OFFLINE_PAY_DETECTED.getTopicName(), 3, (short) 1),
            // 친환경 차 입차 감지
            new NewTopic(KafkaTopic.ECO_CAR_ENTER_DETECTED.getTopicName(), 3, (short) 1),
            // 실패한 메시지를 모아두는 DLT (DB insert 실패, JSON 파싱 실패 같은 경우)
            new NewTopic(KafkaTopic.DEAD_LETTER.getTopicName(), 3, (short) 1)
        );
    }

}