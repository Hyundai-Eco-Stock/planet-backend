package org.phoenix.planet.dto.raffle.raw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberFcmRow {
    private Long memberId;
    private String fcmToken;
}