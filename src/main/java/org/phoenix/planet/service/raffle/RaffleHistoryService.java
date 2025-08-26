package org.phoenix.planet.service.raffle;


import org.phoenix.planet.dto.raffle.raw.RaffleHistoryWithDetail;
import org.phoenix.planet.dto.raffle.raw.WinnerInfo;

import java.time.LocalDate;
import java.util.List;

public interface RaffleHistoryService {
    List<RaffleHistoryWithDetail> findEndedYesterday(LocalDate yesterday);

    void bulkUpdateWinners(List<WinnerInfo> winnerInfos);
}
