package org.phoenix.planet.service.fcm;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.util.fcm.FcmUtil;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmService {

    public void SendEcoStockIssueNotification(List<String> fcmTokens, String body) {

        FcmUtil.sendNotificationToMany(fcmTokens, "에코스톡 지급 완료! \uD83C\uDF89", body,
            "/eco-stock/main");
    }

    public void sendRaffleWinNotification(List<String> fcmTokens, String body) {

        FcmUtil.sendNotificationToMany(fcmTokens, "래플 당첨! \uD83C\uDF89", body,
            "/my-page/raffle-history");
    }

    public void sendPhtiSurvey(List<String> fcmTokens) {

        FcmUtil.sendNotificationToMany(
            fcmTokens,
            "\uD83E\uDD14 나는 어떤 에코 유형일까?",
            "3분만에 확인 가능한 나의 PHTI 성향 테스트!",
            "/phti/survey");
    }

    public void sendCustomNotification(List<String> fcmTokens, String title, String body,
        String path) {

        FcmUtil.sendNotificationToMany(fcmTokens, title, body, path);
    }
}