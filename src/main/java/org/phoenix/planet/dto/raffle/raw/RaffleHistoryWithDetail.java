package org.phoenix.planet.dto.raffle.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaffleHistoryWithDetail {

    private Long raffleId;
    private String raffleName;
    private Long productId;

    private List<RaffleHistory> raffleHistories;
}