package org.phoenix.planet.service.fcm;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.dto.fcm.raw.MemberFcmToken;
import org.phoenix.planet.dto.raffle.raw.WinnerInfo;
import org.phoenix.planet.mapper.MemberDeviceTokenMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<Long, List<String>> findFcmTokensByMemberIds(List<WinnerInfo> winnerInfos) {

        List<Long> memberIds = winnerInfos.stream()
                .map(WinnerInfo::getMemberId)
                .toList();

        List<MemberFcmToken> results = mapper.searchFcmTokensByMemberIds(memberIds);

        return results.stream()
                .collect(Collectors.toMap
                        (
                                MemberFcmToken::getMemberId,
                                MemberFcmToken::getFcmTokens
                        ));
    }
}