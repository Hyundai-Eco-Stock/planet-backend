package org.phoenix.planet.service.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.raffle.raw.WinnerInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final MemberDeviceTokenService memberDeviceTokenService;
    private final FcmService fcmService;

    public void sendRaffleWinNotifications(List<WinnerInfo> winners) {

        if (CollectionUtils.isEmpty(winners)) {
            return;
        }

        Map<Long, List<String>> tokenMap = memberDeviceTokenService.findFcmTokensByMemberIds(winners);

        winners.stream()
                .filter(w -> !CollectionUtils.isEmpty(tokenMap.get(w.getMemberId())))
                .forEach(w -> sendWinNotification(w, tokenMap.get(w.getMemberId())));
    }

    private void sendWinNotification(WinnerInfo winner, List<String> tokens) {

        String body = String.format("축하합니다! %s 래플에 당첨되셨습니다.", winner.getRaffleName());

        log.info(body);

        fcmService.sendRaffleWinNotification(tokens, body);
    }
}
