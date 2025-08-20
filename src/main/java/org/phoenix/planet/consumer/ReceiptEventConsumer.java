package org.phoenix.planet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.phoenix.planet.constant.KafkaTopic;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.dto.offline.request.OfflinePayload;
import org.phoenix.planet.service.card.MemberCardService;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
import org.phoenix.planet.service.eco_stock.EcoStockService;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.phoenix.planet.service.offline.OfflineShopService;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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

    @KafkaListener(topics = KafkaTopic.OFFLINE_PAY_DETECTED_VALUE)
    public void onMessage(String message) throws JsonProcessingException {

        OfflinePayload event = objectMapper.readValue(message,
            OfflinePayload.class);
        log.info("Successfully deserialized event: {}", event);
        Long memberId = memberCardService.searchByCardCompanyIdAndCardNumber(
            event.cardCompanyId(),
            event.cardNumber());

        if (memberId != null) { // 등록된 카드이면
            String shopType = offlineShopService.searchTypeById(event.storeId());
            boolean hasTumbler = event.items().stream()
                .anyMatch(item -> item.productId().equals("TMBL-500"));
            boolean hasPaperBag = event.items().stream()
                .anyMatch(item -> item.productId().equals("PBAG-100"));
            Long ecoStockId = null;

            // 에코스톡 발급 될 것 있나 확인
            if ("CAFE".equals(shopType) && hasTumbler) {
                // 종이백 미사용 에코스톡 id
                ecoStockId = 1L;
            } else if ("FOOD_MALL".equals(shopType) && !hasPaperBag) {
                // 종이백 미사용 에코스톡 id
                ecoStockId = 4L;
            }

            if (ecoStockId != null) {
                // 에코스톡 발급
                ecoStockIssueService.publish(memberId, ecoStockId, 1);
                //  에코스톡 정보 가져오기
                EcoStock ecoStock = ecoStockService.searchById(ecoStockId);
                // FCM 토큰 목록 가져오기
                List<String> memberTokens = memberDeviceTokenService.getTokens(
                    memberId);
                // 푸시 알림 보내기
                fcmService.sendNotificationToMany(memberTokens, "에코스톡 발급",
                    ecoStock.name() + " 1주 발급 완료");
            }

        } else { // 아직 등록되지 않은 카드면 할 수 있는게 없다고 생각...

        }
    }

    @DltHandler
    public void dlt(ConsumerRecord<String, Object> record) {

        log.error("DLT received: {}", record);
    }
}
