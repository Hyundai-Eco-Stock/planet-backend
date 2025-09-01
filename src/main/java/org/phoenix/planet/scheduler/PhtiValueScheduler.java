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
public class PhtiValueScheduler {

    private final FcmService fcmService;
    private final MemberDeviceTokenService memberDeviceTokenService;
    private final MemberPhtiMapper memberPhtiMapper;

    @Scheduled(cron = "0 30 * * * *") // 개발 시
//    @Scheduled(cron = "0 0 12 * * *") // 운영 시
    public void runTask() {

        log.info("✅ 스케줄 실행됨: {}", java.time.LocalDateTime.now());

        List<MemberPhti> MemberPhtiList = memberPhtiMapper.selectAll();
        MemberPhtiList.forEach(memberPhti -> {
            // 기부자 (G) : Giver / Green Challenger (결제 시마다 기부를 추가하는 타입, 가격보다 친환경 상품·기부 등 가치를 우선)
            if (memberPhti.phti().contains("G")) {
                fcmService.sendCustomNotification(
                    memberDeviceTokenService.getTokens(memberPhti.memberId()),
                    "%s 기부자(G) 고객님께 드리는 소식".formatted(memberPhti.phti()),
                    "최근 결제 때 멋진 기부를 해주셨네요 \uD83D\uDE4F 또 한 번의 따뜻한 나눔을 이어가실까요??");
//                fcmService.sendCustomNotification(
//                    memberDeviceTokenService.getTokens(memberPhti.memberId()),
//                    "%s 기부자(G) 고객님께 드리는 소식".formatted(memberPhti.phti()),
//                    "지난번 결제 때 기부를 건너뛰셨군요 \uD83D\uDE42 오늘은 작은 기부로 선한 영향력을 만들어 보시는 건 어떨까요?");
            }
            // 실속파 (P) : Pragmatist / Practical (포인트 적립/할인 위주, 기부는 잘 안 하는 타입)
            else if (memberPhti.phti()
                .contains("P")) {
                fcmService.sendCustomNotification(
                    memberDeviceTokenService.getTokens(memberPhti.memberId()),
                    "%s 실속파(P) 고객님께 드리는 소식".formatted(memberPhti.phti()),
                    "오늘 6시 에코딜 상품 구매 시 추가 에코스톡이 지급됩니다! 절약과 혜택을 동시에 누려보세요 \uD83C\uDF81");
            }
        });
    }
}