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
public class PhtiRaffleScheduler {

    private final FcmService fcmService;
    private final MemberDeviceTokenService memberDeviceTokenService;
    private final MemberPhtiMapper memberPhtiMapper;

    //    @Scheduled(cron = "0 20 * * * *") // 개발 시
    @Scheduled(cron = "0 0 11 * * *") // 운영 시
    @DistributedScheduled(lockKey = "planet:prod:phti:raffle:notification")
    public void runTask() {

        log.info("✅ 스케줄 실행됨: {}", java.time.LocalDateTime.now());

        List<MemberPhti> MemberPhtiList = memberPhtiMapper.selectAll();
        MemberPhtiList.forEach(memberPhti -> {
            // 도전자 (D) : Dare (확률 상관없이 래플에 적극적으로 참여)
            if (memberPhti.phti().contains("D")) {
                fcmService.sendCustomNotification(
                    memberDeviceTokenService.getTokens(memberPhti.memberId()),
                    "%s 도전자(D) 고객님께 드리는 소식".formatted(memberPhti.phti()),
                    "새로운 래플이 오픈되었습니다! 지금 바로 도전해서 행운을 잡아보세요 \uD83C\uDF40");
            }
            // 안정파 (A) : Anchored (거의 참여하지 않고 안정적인 포인트 사용 선호)
            else if (memberPhti.phti().contains("A")) {
                fcmService.sendCustomNotification(
                    memberDeviceTokenService.getTokens(memberPhti.memberId()),
                    "%s 안정파(A) 고객님께 드리는 소식".formatted(memberPhti.phti()),
                    "안정적인 포인트 사용을 선호하시는 고객님께, 적립 포인트로 바로 누릴 수 있는 혜택을 준비했어요 ✨");
            }
        });
    }
}