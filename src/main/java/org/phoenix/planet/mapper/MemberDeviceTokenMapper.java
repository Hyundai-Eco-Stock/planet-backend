package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.fcm.raw.MemberFcmToken;

import java.util.List;

@Mapper
public interface MemberDeviceTokenMapper {

    List<String> searchFcmTokenByMemberId(long memberId);

    void insertToken(long memberId, String fcmToken);

    void updateToken(long memberId, String fcmToken);

    void deleteToken(long memberId, String fcmToken);

    void deleteAllTokensByMemberId(long memberId);

    List<MemberFcmToken> searchFcmTokensByMemberIds(List<Long> memberIds);
}
