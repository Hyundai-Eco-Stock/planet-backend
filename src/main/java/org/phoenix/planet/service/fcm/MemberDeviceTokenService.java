package org.phoenix.planet.service.fcm;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.dto.fcm.raw.MemberFcmToken;
import org.phoenix.planet.dto.raffle.raw.WinnerInfo;
import org.phoenix.planet.mapper.MemberDeviceTokenMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDeviceTokenService {

    private final MemberDeviceTokenMapper mapper;

    public void saveOrUpdate(long memberId, String fcmToken) {

        List<String> tokens = mapper.searchFcmTokenByMemberId(memberId);

        if (tokens.contains(fcmToken)) {
            mapper.updateToken(memberId, fcmToken);
        } else {
            mapper.insertToken(memberId, fcmToken);
        }
    }

    public List<String> getTokens(long memberId) {

        return mapper.searchFcmTokenByMemberId(memberId);
    }

    public List<MemberFcmToken> getTokens(List<Long> memberIdList) {

        return mapper.searchFcmTokensByMemberIds(memberIdList);
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

    public List<String> findAll() {

        return mapper.selectAll();
    }
}