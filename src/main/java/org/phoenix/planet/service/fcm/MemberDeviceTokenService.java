package org.phoenix.planet.service.fcm;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.mapper.MemberDeviceTokenMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDeviceTokenService {

    private final MemberDeviceTokenMapper mapper;

    public void saveOrUpdate(Long memberId, String fcmToken) {

        List<String> tokens = mapper.searchFcmTokenByMemberId(memberId);

        if (tokens.contains(fcmToken)) {
            mapper.updateToken(memberId, fcmToken);
        } else {
            mapper.insertToken(memberId, fcmToken);
        }
    }

    public List<String> getTokens(Long memberId) {

        return mapper.searchFcmTokenByMemberId(memberId);
    }

    public void removeToken(Long memberId, String fcmToken) {

        mapper.deleteToken(memberId, fcmToken);
    }
}