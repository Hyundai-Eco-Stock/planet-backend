package org.phoenix.planet.service.raffle;

import org.phoenix.planet.dto.raffle.RaffleDetailResponse;
import org.phoenix.planet.dto.raffle.RaffleResponse;
import org.phoenix.planet.dto.raffle.raw.WinnerInfo;

import java.time.LocalDate;
import java.util.List;

public interface RaffleService {

    List<RaffleResponse> findAll();

    List<RaffleDetailResponse> findDetailById(Long raffleId);

    void participateRaffle(Long raffleId, Long memberId);

    List<WinnerInfo> raffleWinningProcess(LocalDate yesterday);
}
