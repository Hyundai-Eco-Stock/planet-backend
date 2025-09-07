package org.phoenix.planet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.phoenix.planet.constant.kafka.KafkaTopic;
import org.phoenix.planet.event.PayEvent;
import org.phoenix.planet.service.card.MemberCardService;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfflinePayDetectedConsumer {

    private final ObjectMapper objectMapper;

    private final MemberCardService memberCardService;
    private final EcoStockIssueService ecoStockIssueService;

    @Transactional
    @KafkaListener(
        topics = "#{T(org.phoenix.planet.constant.kafka.KafkaTopic).OFFLINE_PAY_DETECTED.getTopicName()}"
    )
    public void onMessage(String message) throws JsonProcessingException {

        PayEvent event = objectMapper.readValue(
            message,
            KafkaTopic.OFFLINE_PAY_DETECTED.getPayloadType());
        log.info("Successfully deserialized event: {}", event);

        // 카드 정보로 memberId 조회
        Long memberId = memberCardService.searchByCardCompanyIdAndCardNumber(
            event.cardCompanyId(),
            event.cardNumber());

        if (memberId != null) { // 등록된 카드이면
            Long ecoStockId = null;

            if ("TUMBLER_DISCOUNT".equals(event.eventName())) {
                ecoStockId = 1L; // 텀블러 에코스톡 id
            } else if ("PAPER_BAG_NO_USE".equals(event.eventName())) {
                ecoStockId = 4L; // 종이백 미사용 에코스톡 id
            }

            if (ecoStockId != null) {
                // 에코스톡 발급
                ecoStockIssueService.issueStock(memberId, ecoStockId, 1);
            }

        } else { // 아직 등록되지 않은 카드면 할 수 있는게 없다고 생각...
            log.warn("미등록 카드 결제 정보 수신. memberId is null. event: {}", event);
        }
    }

    @DltHandler
    public void dlt(ConsumerRecord<String, Object> record) {

        log.error("DLT received: {}", record);
    }

}
