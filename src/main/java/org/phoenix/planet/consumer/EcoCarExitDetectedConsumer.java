package org.phoenix.planet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.phoenix.planet.constant.KafkaTopic;
import org.phoenix.planet.dto.car.response.MemberCarResponse;
import org.phoenix.planet.event.EcoCarEnterEvent;
import org.phoenix.planet.service.car.MemberCarService;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
import org.phoenix.planet.service.eco_stock.EcoStockService;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EcoCarExitDetectedConsumer {

    private final ObjectMapper objectMapper;
    // 차량 정보
    private final MemberCarService memberCarService;
    // 에코스톡
    private final EcoStockIssueService ecoStockIssueService;


    @Transactional
    @KafkaListener(
        topics = "#{T(org.phoenix.planet.constant.KafkaTopic).ECO_CAR_EXIT_DETECTED.getTopicName()}"
    )
    public void onMessage(String message) throws JsonProcessingException {

        EcoCarEnterEvent event = objectMapper.readValue(
            message,
            KafkaTopic.ECO_CAR_ENTER_DETECTED.getPayloadType());
        log.info("Successfully deserialized event: {}", event);

        if (event == null) {
            throw new IllegalArgumentException("ECO_CAR_EXIT_DETECTED event 정보가 없습니다.");
        }
        MemberCarResponse car = memberCarService.searchByCarNumber(event.carNumber());

        // 친환경 차 에코스톡 발급
        long ecoCarStockId = 3L;
        ecoStockIssueService.issueStock(car.memberId(), ecoCarStockId, 1);
    }

    @DltHandler
    public void dlt(ConsumerRecord<String, Object> record) {

        log.error("DLT received: {}", record);
    }

}
