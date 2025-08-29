package org.phoenix.planet.dto.raffle.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipateRaffleResponse {
    private Long raffleId;   // IN
    private Long memberId;   // IN
    private Integer result;  // OUT
}