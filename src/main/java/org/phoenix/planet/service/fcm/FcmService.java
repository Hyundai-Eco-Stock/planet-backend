package org.phoenix.planet.service.fcm;

import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.util.fcm.FcmUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FcmService {

    public void SendEcoStockIssueNotification(List<String> fcmTokens, String body) {
        
        FcmUtil.sendNotificationToMany(fcmTokens, "에코스톡 지급 완료! \uD83C\uDF89", body);
    }

    public void sendRaffleWinNotification(List<String> fcmTokens, String body) {

        FcmUtil.sendNotificationToMany(fcmTokens, "래플 당첨! \uD83C\uDF89", body);
    }
}