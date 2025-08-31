package org.phoenix.planet.dto.raffle.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WinnerInfo {
    private Long raffleHistoryId;
    private Long memberId;
    private String raffleName;
}
