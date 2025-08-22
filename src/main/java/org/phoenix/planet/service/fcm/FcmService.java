package org.phoenix.planet.service.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmService {

    public void sendNotification(String fcmToken, String title, String body) {

        try {
            Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .putData("click_action", "FLUTTER_NOTIFICATION_CLICK") // 필요시 custom data
                .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message: " + response);
        } catch (Exception e) {
            // TODO: 향후에 kafka 토픽에 담아 다시 시도하도록 할 예정
            // TODO: 커스텀 에러 던지기
            log.warn(e.getMessage());
        }
    }

    public void sendNotificationToMany(List<String> fcmTokens, String title, String body) {

        try {
            MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("Sent messages: {} success, {} failed",
                response.getSuccessCount(),
                response.getFailureCount());

        } catch (Exception e) {
            // TODO: 향후에 kafka 토픽에 담아 다시 시도하도록 할 예정
            // TODO: 커스텀 에러 던지기
            log.warn("Error sending multicast message: {}", e.getMessage());
        }
    }
}