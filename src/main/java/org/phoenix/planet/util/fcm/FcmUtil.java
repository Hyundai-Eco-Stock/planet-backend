package org.phoenix.planet.util.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FcmUtil {

    public static void sendNotificationToMany(List<String> fcmTokens, String title, String body) {

        try {
            MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
//            log.info("Sent messages: {} success, {} failed",
//                response.getSuccessCount(),
//                response.getFailureCount());

        } catch (Exception e) {
            log.warn("Error sending multicast message: {}", e.getMessage());
        }
    }

    public static void sendNotificationToMany(List<String> fcmTokens, String title, String body,
        String path) {

        try {
            MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .putData("path", path) // 👈 추가된 부분: 프론트에서 받을 경로
                .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("Sent messages: {} success, {} failed",
                response.getSuccessCount(),
                response.getFailureCount());

        } catch (Exception e) {
            log.warn("Error sending multicast message: {}", e.getMessage());
        }
    }
}
