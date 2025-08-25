package org.phoenix.planet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.phoenix.planet.constant.KafkaTopic;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.event.PayEvent;
import org.phoenix.planet.service.card.MemberCardService;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
import org.phoenix.planet.service.eco_stock.EcoStockService;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.phoenix.planet.service.offline.OfflinePayHistoryService;
import org.phoenix.planet.service.offline.OfflineProductService;
import org.phoenix.planet.service.offline.OfflineShopService;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfflinePayDetectedConsumer {

    private final ObjectMapper objectMapper;

    private final FcmService fcmService;
    private final EcoStockService ecoStockService;
    private final MemberCardService memberCardService;
    private final OfflineShopService offlineShopService;
    private final EcoStockIssueService ecoStockIssueService;
    private final MemberDeviceTokenService memberDeviceTokenService;
    private final OfflinePayHistoryService offlinePayHistoryService;
    private final OfflineProductService offlineProductService;

    @Transactional
    @KafkaListener(
        topics = "#{T(org.phoenix.planet.constant.KafkaTopic).OFFLINE_PAY_DETECTED.getTopicName()}"
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
            String fcmMessage = null;
            String targetUrl = "/my-page/my-eco-stock";
            if ("TUMBLER_DISCOUNT".equals(event.eventName())) {
                ecoStockId = 1L; // 텀블러 에코스톡 id
                fcmMessage = "텀블러 사용으로 에코스톡 1주가 발급되었습니다.";
            } else if ("PAPER_BAG_NO_USE".equals(event.eventName())) {
                ecoStockId = 4L; // 종이백 미사용 에코스톡 id
                fcmMessage = "종이백 미사용으로 에코스톡 1주가 발급되었습니다.";
            }
            if (ecoStockId != null) {
                // 에코스톡 발급
                ecoStockIssueService.issueStock(memberId, ecoStockId, 1);
                // FCM 토큰 목록 가져오기
                List<String> tokens = memberDeviceTokenService.getTokens(
                    memberId);
                // 에코스톡 정보 가져와 푸시 알림 보내기
                EcoStock ecoStock = ecoStockService.searchById(ecoStockId);
                // TODO: 에코스톡 발급 처리 (어떻게 하지?)
//                offlinePayHistoryService.updateStockIssueStatusTrue(event.offlinePayHistoryId());
                // 알람 전송
                fcmService.SendEcoStockIssueNotification(tokens, fcmMessage);
                log.info("{} 에코스톡 발급 완료", ecoStock.name());
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
