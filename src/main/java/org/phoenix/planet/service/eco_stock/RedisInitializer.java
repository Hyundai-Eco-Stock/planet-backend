package org.phoenix.planet.service.eco_stock;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.raw.EcoStockWithLastPrice;
import org.phoenix.planet.repository.ChartDataSecondRedisRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1) // 가장 먼저 실행
@Slf4j
@RequiredArgsConstructor
public class RedisInitializer implements ApplicationRunner {

    private final ChartDataSecondRedisRepository chartDataSecondRedisRepository;
    private final EcoStockService ecoStockService;

    @Override
    public void run(ApplicationArguments args){

        log.info("애플리케이션 시작 - Redis 필수 데이터 초기화 중...");

        try {
            // Redis 초기화 로직

            List<EcoStockWithLastPrice> ecoStocks = ecoStockService.findAllWithLastPrice();

            for (EcoStockWithLastPrice ecoStock : ecoStocks) {

                chartDataSecondRedisRepository.initializeStockPrice(ecoStock.getId(),
                    ecoStock.getLastPrice(), ecoStock.getQuantity());
            }

        } catch (Exception e) {
            log.error("Redis 초기화 실패 - 애플리케이션 종료", e);
            System.exit(1); // 초기화 실패시 앱 종료
        }
    }
}