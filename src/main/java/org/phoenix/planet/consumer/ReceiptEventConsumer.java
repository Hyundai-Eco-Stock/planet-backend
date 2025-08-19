package org.phoenix.planet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.phoenix.planet.constant.KafkaTopic;
import org.phoenix.planet.dto.offline_pay.request.OfflinePayload;
import org.phoenix.planet.service.card.MemberCardService;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptEventConsumer {

    private final EcoStockIssueService ecoStockIssueService;
    private final MemberCardService memberCardService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopic.OFFLINE_PAY_DETECTED_VALUE)
    public void onMessage(String message) throws JsonProcessingException {

        OfflinePayload event = objectMapper.readValue(message,
            OfflinePayload.class);
        log.info("Successfully deserialized event: {}", event);
        Long memberId = memberCardService.searchByCardCompanyIdAndCardNumber(event.cardCompanyId(),
            event.cardNumber());
        log.info("해당 카드 유저 찾음 member id: {}", memberId);
        if (memberId != null) { // 등록된 카드
            ecoStockIssueService.publish(memberId, 4L, 1);
        } else { // 등록되지 않은 카드
            // TODO
        }
    }

    @DltHandler
    public void dlt(ConsumerRecord<String, Object> record) {

        log.error("DLT received: {}", record);
    }
}
