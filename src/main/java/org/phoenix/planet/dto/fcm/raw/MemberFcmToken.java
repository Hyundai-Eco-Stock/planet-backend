package org.phoenix.planet.dto.fcm.raw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberFcmToken {
    private Long memberId;
    private List<String> fcmTokens;
}