package org.phoenix.planet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.phoenix.planet.constant.KafkaTopic;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.dto.offline.raw.KafkaOfflinePayInfo;
import org.phoenix.planet.service.card.MemberCardService;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
import org.phoenix.planet.service.eco_stock.EcoStockService;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.phoenix.planet.service.offline.OfflinePayHistoryService;
import org.phoenix.planet.service.offline.OfflineShopService;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptEventConsumer {

    private final ObjectMapper objectMapper;

    private final FcmService fcmService;
    private final EcoStockService ecoStockService;
    private final MemberCardService memberCardService;
    private final OfflineShopService offlineShopService;
    private final EcoStockIssueService ecoStockIssueService;
    private final MemberDeviceTokenService memberDeviceTokenService;
    private final OfflinePayHistoryService offlinePayHistoryService;

    private static final List<Long> tumblerDiscountProductIdList = Arrays.asList(
        1099L, 2099L, 3099L, 4099L);
    private static final List<Long> paperBagProductIdList = Arrays.asList(
        1199L, 2199L, 3199L, 4199L);

    @Transactional
    @KafkaListener(topics = KafkaTopic.OFFLINE_PAY_DETECTED_VALUE)
    public void onMessage(String message) throws JsonProcessingException {

        KafkaOfflinePayInfo event = objectMapper.readValue(
            message,
            KafkaTopic.OFFLINE_PAY_DETECTED.getPayloadType());
        log.info("Successfully deserialized event: {}", event);

        // 카드 정보로 memberId 조회
        Long memberId = memberCardService.searchByCardCompanyIdAndCardNumber(
            event.cardCompanyId(),
            event.cardNumber());
        
        if (memberId != null) { // 등록된 카드이면
            // 가게 타입에 따른 에코스톡 발급 여부 확인
            String shopType = offlineShopService.searchTypeById(event.shopId());
            boolean hasTumbler = event.items().stream()
                .anyMatch(it -> tumblerDiscountProductIdList.contains(it.productId()));

            boolean hasPaperBag = event.items().stream()
                .anyMatch(it -> paperBagProductIdList.contains(it.productId()));

            // 에코스톡 발급 될 것 있나 확인
            Long ecoStockId = null;
            String fcmMessage = null;
            if ("CAFE".equals(shopType) && hasTumbler) {
                ecoStockId = 1L; // 텀블러 에코스톡 id
                fcmMessage = "텀블러 사용으로 에코스톡 1주가 발급되었습니다.";
            } else if ("FOOD_MALL".equals(shopType) && !hasPaperBag) {
                ecoStockId = 4L; // 종이백 미사용 에코스톡 id
                fcmMessage = "종이백 미사용으로 에코스톡 1주가 발급되었습니다.";
            }

            if (ecoStockId != null && fcmMessage != null) {
                // 에코스톡 발급
                ecoStockIssueService.publish(memberId, ecoStockId, 1);
                // FCM 토큰 목록 가져오기
                List<String> memberTokens = memberDeviceTokenService.getTokens(
                    memberId);
                // 에코스톡 정보 가져와 푸시 알림 보내기
                EcoStock ecoStock = ecoStockService.searchById(ecoStockId);
                // 에코스톡 발급 처리
                offlinePayHistoryService.updateStockIssueStatusTrue(event.offlinePayHistoryId());
                // 알람 전송
                fcmService.sendNotificationToMany(
                    memberTokens,
                    "\uD83C\uDF89 에코스톡 지급 완료!",
                    fcmMessage);
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
