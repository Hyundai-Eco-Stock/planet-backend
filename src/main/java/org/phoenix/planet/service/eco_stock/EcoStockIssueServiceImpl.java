package org.phoenix.planet.service.eco_stock;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.mapper.EcoStockIssueMapper;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EcoStockIssueServiceImpl implements EcoStockIssueService {

    private final FcmService fcmService;
    private final EcoStockIssueMapper ecoStockIssueMapper;
    private final MemberStockInfoService memberStockInfoService;
    private final MemberDeviceTokenService memberDeviceTokenService;

    @Override
    @Transactional
    public void issueStock(long memberId, long ecoStockId, int amount) {
        // 유효성 검사
        if (amount <= 0) {
            throw new IllegalArgumentException("잘못된 amount 입니다.");
        }
        // 에코스톡 발급
        for (int i = 0; i < amount; i++) {
            ecoStockIssueMapper.insert(memberId, ecoStockId);
        }
        // FCM 토큰 목록 가져와 푸시 알람 전송
        List<String> tokens = memberDeviceTokenService.getTokens(memberId);
        String fcmMessage = createFcmMessage(ecoStockId, amount);
        fcmService.SendEcoStockIssueNotification(tokens, fcmMessage);
        // 유저의 에코스톡 보유 정보 수정
        memberStockInfoService.updateOrInsert(memberId, ecoStockId, amount);
        log.info("에코스톡 발급 완료");
    }

    private String createFcmMessage(long ecoStockId, int amount) {

        String fcmMessage;
        if (ecoStockId == 1L) {
            fcmMessage = "텀블러 사용으로 에코스톡 %d주가 발급되었습니다.".formatted(amount);
        } else if (ecoStockId == 2L) {
            fcmMessage = "친환경 제품 구매로 에코스톡 %d주가 발급되었습니다.".formatted(amount);
        } else if (ecoStockId == 3L) {
            fcmMessage = "친환경 차량 입차가 감지되어 에코스톡 %d주가 발급되었습니다.".formatted(amount);
        } else if (ecoStockId == 4L) {
            fcmMessage = "종이백 미사용으로 에코스톡 %d주가 발급되었습니다.".formatted(amount);
        } else if (ecoStockId == 5L) {
            fcmMessage = "봉시활동으로 에코스톡 %d주가 발급되었습니다.".formatted(amount);
        } else if (ecoStockId == 6L) {
            fcmMessage = "고객님의 소중한 기부로 에코스톡 %d주가 발급되었습니다.".formatted(amount);
        } else {
            throw new IllegalArgumentException("잘못된 eco stock id 입니다.");
        }
        return fcmMessage;
    }
}
