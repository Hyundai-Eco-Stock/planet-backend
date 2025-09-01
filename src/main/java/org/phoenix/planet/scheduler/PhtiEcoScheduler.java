package org.phoenix.planet.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.phti.raw.MemberPhti;
import org.phoenix.planet.mapper.MemberPhtiMapper;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class PhtiEcoScheduler {

    private final FcmService fcmService;
    private final MemberDeviceTokenService memberDeviceTokenService;
    private final MemberPhtiMapper memberPhtiMapper;

    @Scheduled(cron = "0 0 * * * *") // 개발 시
//    @Scheduled(cron = "0 0 9 * * *") // 운영 시
    public void runTask() {

        log.info("✅ 스케줄 실행됨: {}", java.time.LocalDateTime.now());

        List<MemberPhti> MemberPhtiList = memberPhtiMapper.selectAll();
        MemberPhtiList.forEach(memberPhti -> {
            // 탐험가 (E) : Explorer (다양한 에코스톡을 자주 발행하고 거래도 활발)
            if (memberPhti.phti().contains("E")) {
                // 매일 1시
                fcmService.sendCustomNotification(
                    memberDeviceTokenService.getTokens(memberPhti.memberId()),
                    "%s 탐험가(E) 고객님께 드리는 소식".formatted(memberPhti.phti()),
                    "오늘도 새로운 에코스톡이 준비되어 있어요! 지금 바로 탐험해 보세요 \uD83C\uDF31");
            }
            // 절약가 (C) : Collector (거래는 적고 조용히 포인트만 차곡차곡 모음)
            else if (memberPhti.phti()
                .contains("C")) {
                fcmService.sendCustomNotification(
                    memberDeviceTokenService.getTokens(memberPhti.memberId()),
                    "%s 절약가(C) 고객님께 드리는 소식".formatted(memberPhti.phti()),
                    "고객님께서 모으신 포인트가 꽤 쌓였어요! 조금만 더 모으면 특별 혜택을 받으실 수 있습니다 \uD83D\uDC8E");
            }
        });
    }
}