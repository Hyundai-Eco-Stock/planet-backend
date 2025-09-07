package org.phoenix.planet.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.DistributedScheduled;
import org.phoenix.planet.dto.phti.raw.MemberPhti;
import org.phoenix.planet.mapper.MemberPhtiMapper;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PhtiPointScheduler {

    private final FcmService fcmService;
    private final MemberDeviceTokenService memberDeviceTokenService;
    private final MemberPhtiMapper memberPhtiMapper;

    //    @Scheduled(cron = "0 10 * * * *") // 개발 시
    @Scheduled(cron = "0 0 10 * * *") // 운영 시
    @DistributedScheduled(lockKey = "planet:prod:phti:point:notification")
    public void runTask() {

        log.info("✅ 스케줄 실행됨: {}", java.time.LocalDateTime.now());

        List<MemberPhti> MemberPhtiList = memberPhtiMapper.selectAll();
        MemberPhtiList.forEach(memberPhti -> {
            // 저축러 (S) : Saver (모으는 걸 좋아하고 잘 안 씀)
            if (memberPhti.phti().contains("S")) {
                fcmService.sendCustomNotification(
                    memberDeviceTokenService.getTokens(memberPhti.memberId()),
                    "%s 저축러(S) 고객님께 드리는 소식".formatted(memberPhti.phti()),
                    "고객님의 꾸준한 저축 덕분에 포인트가 알차게 쌓이고 있어요. 곧 큰 혜택으로 바꿔드릴게요 \uD83D\uDCB0",
                    "/my-page/my-assets");
            }
            // 즉시러 (I) : Immediate (포인트 생기면 바로바로 교환)
            else if (memberPhti.phti().contains("I")) {
                fcmService.sendCustomNotification(
                    memberDeviceTokenService.getTokens(memberPhti.memberId()),
                    "%s 즉시러(I) 고객님께 드리는 소식".formatted(memberPhti.phti()),
                    "새로운 포인트 교환 상품이 업데이트되었습니다! 지금 바로 교환하고 즉시 혜택을 누리세요 ⚡",
                    "/my-page/my-assets");
            }
        });
    }
}