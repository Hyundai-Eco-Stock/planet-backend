package org.phoenix.planet.service.fcm;

import org.phoenix.planet.dto.raffle.raw.WinnerInfo;

import java.util.List;


public interface NotificationService {

    void sendRaffleWinNotifications(List<WinnerInfo> winners);
}
