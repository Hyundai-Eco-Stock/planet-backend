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
            // 에러가 아니기 때문에 처리 안해도 상관 없음
            log.warn(e.getMessage());
        }
    }

    public void sendNotificationToMany(
        List<String> fcmTokens,
        String title,
        String body,
        String targetUrl) {

        try {
            // 푸시 노티피케이션 구성
            Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

            // Webpush-specific 설정: 알림 및 클릭 시 열릴 링크 지정
            WebpushNotification webNotification = WebpushNotification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

            WebpushConfig webpushConfig =
                (targetUrl != null) ?
                    WebpushConfig.builder()
                        .setNotification(webNotification)
                        .putData("target_url", targetUrl)
                        .build()
                    : WebpushConfig.builder()
                        .setNotification(webNotification)
                        .build();

            // 멀티캐스트 메시지 조립
            MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(notification) // 모바일 노티피케이션
                .setWebpushConfig(webpushConfig) // 웹 전용 설정
                .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("Sent messages: {} success, {} failed",
                response.getSuccessCount(),
                response.getFailureCount());

        } catch (Exception e) {
            // 에러가 아니기 때문에 처리 안해도 상관 없음
            log.warn("Error sending multicast message: {}", e.getMessage());
        }
    }
}