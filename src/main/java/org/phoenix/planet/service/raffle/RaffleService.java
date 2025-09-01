package org.phoenix.planet.service.raffle;

import org.phoenix.planet.dto.raffle.RaffleDetailResponse;
import org.phoenix.planet.dto.raffle.RaffleResponse;
import org.phoenix.planet.dto.raffle.raw.WinnerInfo;

import java.time.LocalDate;
import java.util.List;
import org.phoenix.planet.dto.raffle.response.ParticipateRaffleResponse;

public interface RaffleService {

    List<RaffleResponse> findAll();

    RaffleDetailResponse findDetailById(Long raffleId);

    ParticipateRaffleResponse participateRaffle(Long raffleId, Long memberId);

    List<WinnerInfo> raffleWinningProcess(LocalDate yesterday);
}
