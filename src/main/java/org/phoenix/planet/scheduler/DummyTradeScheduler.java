package org.phoenix.planet.scheduler;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.DistributedScheduled;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;
import org.phoenix.planet.repository.ChartDataSecondRedisRepository;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
import org.phoenix.planet.service.eco_stock.EcoStockService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DummyTradeScheduler {

    private final ChartDataSecondRedisRepository chartRepo;
    private final TaskScheduler taskScheduler;
    private final SecureRandom rnd = new java.security.SecureRandom();
    private final EcoStockIssueService ecoStockIssueService;

    private final EcoStockService ecoStockService;

    private static final List<Long> STOCK_IDS = java.util.List.of(1L,2L,3L,4L,6L);
    private static final double HOURLY_TARGET = 50.0;            // 시간당 목표
    private static final int BURST_CAP_PER_MIN = 3;               // 분당 최대 실행 한도
    private static final double SELL_RATIO = 0.55;
    private static final int MIN_QTY = 6, MAX_QTY = 12;
//    private static final int ACTIVE_START = 9, ACTIVE_END = 23;   // 9~23시만

    private static List<Long> memberList = List.of(1000001L, 1000002L, 1000003L);

    @Scheduled(cron = "5 * * * * *", zone = "Asia/Seoul") // 매 분 00초에 오케스트레이터만 실행
    @DistributedScheduled(lockKey = "ecoStock:dummy-trade:orchestrator")
    public void orchestrateOneMinute() {
        var now = java.time.LocalDateTime.now();
        int h = now.getHour();
//        if (h < ACTIVE_START || h > ACTIVE_END) return;

        // 분당 기대 거래수 (포아송)
        double lambda = HOURLY_TARGET / 60.0;
        int trades = samplePoisson(lambda);
        trades = Math.min(trades, BURST_CAP_PER_MIN);

        if (trades == 0) return;

        // 이번 분(60초) 안에 랜덤 초에 분산
        for (int i = 0; i < trades; i++) {
            int offsetSec = rnd.nextInt(60); // 0~59
            Instant when = Instant.now().plusSeconds(offsetSec);

            taskScheduler.schedule(() -> {
                LocalDateTime t = LocalDateTime.now();
                doOneTrade(t);
            }, when);
        }
        log.info("🧪 Orchestrated {} trades this minute (Poisson λ={}): scattered randomly", trades, String.format("%.3f", lambda));
    }

    private void doOneTrade(LocalDateTime now) {
        int qty = MIN_QTY + rnd.nextInt(MAX_QTY - MIN_QTY + 1);
        boolean sell = rnd.nextDouble() < SELL_RATIO;
        int tradeQuantity = sell ? qty : -qty;
        int reply = Math.abs(tradeQuantity);

        int successCount =0;
        for (int i = 0; i < reply; i++) {
            Long stockId = STOCK_IDS.get(rnd.nextInt(STOCK_IDS.size()));
            Long memberId =  memberList.get(rnd.nextInt(memberList.size()));

            try {
//                UnifiedUpdateResult res = chartRepo.processTradeWithChart(stockId, 1, now);

                if (sell) {
                    SellStockRequest request = SellStockRequest.builder()
                        .sellCount(1)
                        .ecoStockId(stockId)
                        .build();

                    ecoStockService.sellStock(memberId, request);
                } else {

                    ecoStockIssueService.processIssue(memberId,stockId);
                }

                log.trace("🧪 {} {}ea stockId={}",
                    sell ? "SELL" : "BUY", qty, stockId);
                successCount++;
            } catch (Exception e) {
                log.warn("🧪 DummyTrade fail: stockId={}, q={}, err={}", stockId, tradeQuantity, e.getMessage());
//                ecoStockIssueService.processIssue(memberId,stockId);
            }
        }

        log.info("reply: {} successCount: {}",reply,successCount);
    }

    // 포아송 샘플러
    private int samplePoisson(double lambda) {
        double L = Math.exp(-lambda);
        int k = 0; double p = 1.0;
        do { k++; p *= rnd.nextDouble(); } while (p > L);
        return k - 1;
    }
}
