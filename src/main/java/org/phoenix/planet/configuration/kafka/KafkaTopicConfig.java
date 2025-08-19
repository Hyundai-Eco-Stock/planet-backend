package org.phoenix.planet.configuration.kafka;

import java.util.List;
import org.apache.kafka.clients.admin.NewTopic;
import org.phoenix.planet.constant.KafkaTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public List<NewTopic> topics() {

        return List.of(
            // 종이백 미사용 - 영수증 발급 내역 감지
            new NewTopic(KafkaTopic.OFFLINE_PAY_DETECTED.getValue(), 3, (short) 1),
            // 실패한 메시지를 모아두는 DLT (DB insert 실패, JSON 파싱 실패 같은 경우)
            new NewTopic(KafkaTopic.DEAD_LETTER.getValue(), 3, (short) 1)
        );
    }

}