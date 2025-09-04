package org.phoenix.planet.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.DistributedScheduled;
import org.phoenix.planet.mapper.MemberMapper;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnsurveyPhtiMemberNotificationScheduler {

    private final FcmService fcmService;
    private final MemberDeviceTokenService memberDeviceTokenService;
    private final MemberMapper memberMapper;

    //    @Scheduled(cron = "0 0 * * * *") // 개발 시
    @Scheduled(cron = "0 30 12 * * *") // 운영 시
    @DistributedScheduled(lockKey = "planet:prod:phti:not-survey-member:notification")
    public void runTask() {

        log.info("✅ 스케줄 실행됨: {}", java.time.LocalDateTime.now());

        List<Long> unsurveyedPhtiMemberIds = memberMapper.selectUnsurveyPhtiMemberList();
        List<String> tokens = memberDeviceTokenService.getTokens(unsurveyedPhtiMemberIds)
            .stream()
            .flatMap(memberFcmToken -> memberFcmToken.getFcmTokens().stream())
            .toList();

        fcmService.sendPhtiSurvey(tokens);
    }
}