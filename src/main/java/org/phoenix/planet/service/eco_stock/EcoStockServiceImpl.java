package org.phoenix.planet.service.eco_stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.RaffleError;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.dto.eco_stock.raw.EcoStockUpdatePriceRecord;
import org.phoenix.planet.dto.eco_stock.raw.EcoStockWithLastPrice;
import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock.raw.PointResult;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;
import org.phoenix.planet.dto.eco_stock.response.UnifiedUpdateResult;
import org.phoenix.planet.dto.eco_stock_info.response.SellResponse;
import org.phoenix.planet.error.raffle.RaffleException;
import org.phoenix.planet.mapper.EcoStockMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EcoStockServiceImpl implements EcoStockService {

    private final EcoStockMapper ecoStockMapper;
    private final MemberStockInfoService memberStockInfoService;
    private final StockTradeProcessor stockTradeProcessor;

    @Override
    public EcoStock searchById(long ecoStockId) {

        return ecoStockMapper.selectById(ecoStockId);
    }

    @Override
    public List<EcoStock> findAll() {

        return ecoStockMapper.findAll();
    }

    @Override
    public void updateQuantityById(Long stockId, int updateQuantity) {

        ecoStockMapper.updateQuantityById(stockId, updateQuantity);
    }

    @Override
    public List<EcoStockUpdatePriceRecord> findAllHistory(LocalDateTime targetTime) {

        return ecoStockMapper.findAllHistory(targetTime);
    }

    @Override
    public SellResponse sellStock(Long memberId, SellStockRequest request) {

        // 1. 사용자 주식 보유량 체크
        MemberStockInfo memberStockInfo = memberStockInfoService.validateUserStock(memberId,
            request);

        UnifiedUpdateResult result = stockTradeProcessor.executeSellTradeAndBroadcast(
            request.getEcoStockId(), request.getSellCount());
        //프로시저로 빼기
        PointResult pointResult = processUserTransactionAndPoint(memberId, request,
            result, memberStockInfo);

        // 반환값 빌드
        SellResponse sellResponse = SellResponse.builder()
            .executedPrice(result.getExecutedPrice())
            .currentTotalQuantity(Optional.ofNullable(pointResult)
                .map(PointResult::getCurrentTotalQuantity)
                .orElse(0))
            .currentTotalAmount(Optional.ofNullable(pointResult)
                .map(PointResult::getCurrentTotalAmount)
                .map(this::roundToTwoDecimals)
                .orElse(0.0))
            .memberPoint(Optional.ofNullable(pointResult)
                .map(PointResult::getMemberPoint)
                .map(this::roundToTwoDecimals)
                .orElse(0.0))
            .build();

        log.info("sell stock result:{}", sellResponse);

        return sellResponse;
    }

    // 유틸 메서드 생성
    private double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
    }


    @Override
    public PointResult processUserTransactionAndPoint(Long loginMemberId, SellStockRequest request,
        UnifiedUpdateResult result, MemberStockInfo memberStockInfo) {

        PointResult pointResult = new PointResult();

        ecoStockMapper.callSellStockUserTransaction(loginMemberId, result, request.getSellCount(),
            memberStockInfo, pointResult);

        log.info("{}", result);

        // 성공 여부 체크
        if (pointResult.getSuccess() == null) {
            throw new RaffleException(RaffleError.PROCEDURE_ERROR);
        }

        switch (pointResult.getSuccess()) {
            case 1: // 성공
                log.info("✅ 판매 성공: {}", pointResult.getMessage());
                break;
            case -3: // 보유 주식 없음
                throw new RaffleException(RaffleError.INSUFFICIENT_STOCK);
            case -99: // 프로시저 내부 예외
                throw new RaffleException(RaffleError.PROCEDURE_ERROR);
            default: // 그 외 알 수 없는 코드
                log.error("{}): {}", pointResult.getSuccess(), pointResult.getMessage());
                throw new RaffleException(RaffleError.RAFFLE_SYSTEM_ERROR);
        }
        return pointResult;
    }

    @Override
    public List<EcoStockWithLastPrice> findAllWithLastPrice() {

        return ecoStockMapper.findAllWithLastPrice();
    }
}
