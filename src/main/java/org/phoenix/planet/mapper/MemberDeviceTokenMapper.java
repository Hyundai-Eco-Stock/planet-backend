package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDeviceTokenMapper {

    List<String> searchFcmTokenByMemberId(long memberId);

    void insertToken(long memberId, String fcmToken);

    void updateToken(long memberId, String fcmToken);

    void deleteToken(long memberId, String fcmToken);

    void deleteAllTokensByMemberId(long memberId);
}
