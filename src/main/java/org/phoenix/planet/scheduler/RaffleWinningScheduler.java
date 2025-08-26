package org.phoenix.planet.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.service.raffle.RaffleService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RaffleWinningScheduler {

    private final RaffleService raffleService;

    @Scheduled(cron = "0 0 0 * * *") //
//    @Scheduled(cron = "0/30 * * * * *") //테스트용
//    @DistributedScheduled(lockKey = "raffle:winning:update:lock")
    public void raffleWinningProcess() {
//        LocalDate yesterday = LocalDate.of(2025,9,1).minusDays(1L);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("RaffleWinningScheduler");

        raffleService.raffleWinningProcess(yesterday);

        log.info("Raffle Winning Process finished");
    }
}

