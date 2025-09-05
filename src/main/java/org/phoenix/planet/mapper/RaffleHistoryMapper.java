package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.raffle.raw.RaffleHistoryWithDetail;

import java.time.LocalDate;
import java.util.List;
import org.phoenix.planet.dto.raffle.response.RaffleEntryStatus;

@Mapper
public interface RaffleHistoryMapper {
    List<RaffleHistoryWithDetail> findEndedYesterday(LocalDate yesterday);

    void bulkUpdateWinners(List<Long> raffleHistoryIdList);

    boolean checkRaffleEntry(Long memberId, Long raffleId);
}
