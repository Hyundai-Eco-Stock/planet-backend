package org.phoenix.planet.dto.raffle.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaffleHistory {
    private Long raffleHistoryId;
    private Long memberId;
}