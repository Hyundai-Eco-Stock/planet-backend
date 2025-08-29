package org.phoenix.planet.service.raffle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.raffle.raw.RaffleHistoryWithDetail;
import org.phoenix.planet.dto.raffle.raw.WinnerInfo;
import org.phoenix.planet.mapper.RaffleHistoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RaffleHistoryServiceImpl implements RaffleHistoryService {

    private final RaffleHistoryMapper raffleHistoryMapper;

    @Override
    public List<RaffleHistoryWithDetail> findEndedYesterday(LocalDate yesterday) {

        return raffleHistoryMapper.findEndedYesterday(yesterday);
    }

    @Override
    @Transactional
    public void bulkUpdateWinners(List<WinnerInfo> winnerInfos) {

        List<Long> ids = winnerInfos.stream()
                .map(WinnerInfo::getRaffleHistoryId)
                .toList();

        if (ids.isEmpty()) return;

        raffleHistoryMapper.bulkUpdateWinners(ids);
    }
}
