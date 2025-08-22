package org.phoenix.planet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.phoenix.planet.constant.KafkaTopic;
import org.phoenix.planet.dto.car.response.MemberCarResponse;
import org.phoenix.planet.dto.car_access.raw.EcoCarEnterEvent;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
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
public class EcoCarEnterDetectedConsumer {

    private final ObjectMapper objectMapper;

    // 알림
    private final FcmService fcmService;
    private final MemberDeviceTokenService memberDeviceTokenService;

    // 차량 정보
    private final MemberCarService memberCarService;

    // 에코스톡
    private final EcoStockService ecoStockService;
    private final EcoStockIssueService ecoStockIssueService;


    @Transactional
    @KafkaListener(topics = KafkaTopic.ECO_CAR_ENTER_DETECTED_VALUE)
    public void onMessage(String message) throws JsonProcessingException {

        EcoCarEnterEvent event = objectMapper.readValue(
            message,
            KafkaTopic.ECO_CAR_ENTER_DETECTED.getPayloadType());
        log.info("Successfully deserialized event: {}", event);

        MemberCarResponse car = memberCarService.searchByCarNumber(event.carNumber());
        // 에코스톡 정보
        long ecoCarStockId = 3L;
        EcoStock ecoStock = ecoStockService.searchById(ecoCarStockId);
        // 에코스톡 발급
        ecoStockIssueService.issueStock(car.memberId(), ecoStock.id(), 1);

        // 알림
        List<String> tokens = memberDeviceTokenService.getTokens(car.memberId());
        fcmService.sendNotificationToMany(
            tokens,
            "\uD83C\uDF89 에코스톡 지급 완료!",
            "친환경 차량 입차가 감지되어 에코스톡 1주가 발급되었습니다.");
    }

    @DltHandler
    public void dlt(ConsumerRecord<String, Object> record) {

        log.error("DLT received: {}", record);
    }

}
