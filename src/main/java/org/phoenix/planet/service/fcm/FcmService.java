package org.phoenix.planet.service.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.util.fcm.FcmUtil;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmService {

    public void SendEcoStockIssueNotification(List<String> fcmTokens, String body) {
        
        FcmUtil.sendNotificationToMany(fcmTokens, "에코스톡 지급 완료! \uD83C\uDF89", body);
    }
}