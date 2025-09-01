package org.phoenix.planet.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class EcoDealScheduler {

    private final FcmService fcmService;
    private final MemberDeviceTokenService memberDeviceTokenService;

    // 매일 오후 6시에 실행
    @Scheduled(cron = "0 0 18 * * *")
    public void runTask() {

        log.info("✅ 스케줄 실행됨: {}", java.time.LocalDateTime.now());
        // 모든 사용자에게 오후 6시에 푸시 알림
        List<String> fcmTokens = memberDeviceTokenService.findAll();
        fcmService.sendCustomNotification(
            fcmTokens,
            "🌱 오늘 오후 6시! 새로운 에코딜 식품이 공개되었습니다",
            "한정 혜택을 지금 바로 확인해 보세요 🚀");
    }
}