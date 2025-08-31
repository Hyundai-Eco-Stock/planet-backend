package org.phoenix.planet.service.eco_stock;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.OrderError;
import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock.raw.StockData;
import org.phoenix.planet.dto.order.raw.OrderConfirmResult;
import org.phoenix.planet.dto.order.response.EcoStockIssueResponse;
import org.phoenix.planet.error.order.OrderException;
import org.phoenix.planet.mapper.EcoStockIssueMapper;
import org.phoenix.planet.mapper.EcoStockPriceHistoryMapper;
import org.phoenix.planet.mapper.MemberStockInfoMapper;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EcoStockIssueServiceImpl implements EcoStockIssueService {

    private final FcmService fcmService;
    private final EcoStockIssueMapper ecoStockIssueMapper;
    private final EcoStockPriceHistoryMapper stockPriceHistoryMapper;
    private final MemberStockInfoMapper memberStockInfoMapper;
    private final MemberStockInfoService memberStockInfoService;
    private final MemberDeviceTokenService memberDeviceTokenService;
  
    // 스톡 종류별 ID 상수
    private static final Long ECO_PRODUCT_STOCK_ID = 2L;  // 친환경 상품 스톡
    private static final Long DONATION_STOCK_ID = 6L;     // 기부 스톡

    @Override
    @Transactional
    public void issueStock(long memberId, long ecoStockId, int amount) {
        // 유효성 검사
        if (amount <= 0) {
            throw new IllegalArgumentException("잘못된 amount 입니다.");
        }
        // 에코스톡 발급
        for (int i = 0; i < amount; i++) {
            ecoStockIssueMapper.insert(memberId, ecoStockId);
        }
        // FCM 토큰 목록 가져와 푸시 알람 전송
        List<String> tokens = memberDeviceTokenService.getTokens(memberId);
        String fcmMessage = createFcmMessage(ecoStockId, amount);
        fcmService.SendEcoStockIssueNotification(tokens, fcmMessage);
        // 유저의 에코스톡 보유 정보 수정
        memberStockInfoService.updateOrInsert(memberId, ecoStockId, amount);
        log.info("에코스톡 발급 완료");
    }

    private String createFcmMessage(long ecoStockId, int amount) {

        String fcmMessage;
        if (ecoStockId == 1L) {
            fcmMessage = "텀블러 사용으로 에코스톡 %d주가 발급되었습니다.".formatted(amount);
        } else if (ecoStockId == 2L) {
            fcmMessage = "친환경 제품 구매로 에코스톡 %d주가 발급되었습니다.".formatted(amount);
        } else if (ecoStockId == 3L) {
            fcmMessage = "친환경 차량 입차가 감지되어 에코스톡 %d주가 발급되었습니다.".formatted(amount);
        } else if (ecoStockId == 4L) {
            fcmMessage = "종이백 미사용으로 에코스톡 %d주가 발급되었습니다.".formatted(amount);
        } else if (ecoStockId == 5L) {
            fcmMessage = "봉시활동으로 에코스톡 %d주가 발급되었습니다.".formatted(amount);
        } else if (ecoStockId == 6L) {
            fcmMessage = "고객님의 소중한 기부로 에코스톡 %d주가 발급되었습니다.".formatted(amount);
        } else {
            throw new IllegalArgumentException("잘못된 eco stock id 입니다.");
        }
        return fcmMessage;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public EcoStockIssueResponse issueEcoStock(OrderConfirmResult orderConfirmResult, Long memberId) {
        log.info("구매 확정 에코스톡 발급 - orderHistoryId : {}, memberId : {}", orderConfirmResult.orderHistoryId(), memberId);

        int totalIssuedCount = 0;
        int totalStockValue = 0;

        try {
            // 전체 스톡 최신 가격들 조회
            List<StockData> allLatestPrices = stockPriceHistoryMapper.findAllLatestPrice();

            // 필요한 스톡 가격 찾기
            StockData ecoProductPrice = allLatestPrices.stream()
                .filter(stock -> stock.getEcoStockId().equals(ECO_PRODUCT_STOCK_ID))
                .findFirst()
                .orElse(null);

            StockData donationPrice = allLatestPrices.stream()
                .filter(stock -> stock.getEcoStockId().equals(DONATION_STOCK_ID))
                .findFirst()
                .orElse(null);

            if (ecoProductPrice == null) {
                throw new OrderException(OrderError.ECOSTOCK_PRICE_NOT_FOUND);
            }

            // 친환경 상품 스톡 발급 (무조건 1주)
            // 현재 친환경 스톡 보유량 조회
            MemberStockInfo ecoProductStock = memberStockInfoMapper.findPersonalStockInfoById(memberId, ECO_PRODUCT_STOCK_ID);

            // stock_issue에 발급 기록 저장
            ecoStockIssueMapper.insert(memberId, ECO_PRODUCT_STOCK_ID);

            // MEMBER_STOCK_INFO 업데이트
            if (ecoProductStock.memberStockInfoId() == null) {
                memberStockInfoMapper.insertMemberStockInfo(memberId, ECO_PRODUCT_STOCK_ID, 1, ecoProductPrice.getStockPrice().intValue());
            } else {
                int newQuantity = ecoProductStock.currentTotalQuantity() + 1;
                long newAmount = ecoProductStock.currentTotalAmount() + ecoProductPrice.getStockPrice();
                memberStockInfoMapper.updateMemberStockInfo(ecoProductStock.memberStockInfoId(), newQuantity, newAmount);
            }

            totalIssuedCount++;
            totalStockValue = totalStockValue + ecoProductPrice.getStockPrice().intValue();
            log.info("친환경 상품 스톡 발급 완료 - memberId: {}, stockPrice: {}", memberId, ecoProductPrice.getStockPrice());

            // 기부 스톡 발급 (기부금이 있는 경우만)
            if (orderConfirmResult.donationPrice() != null && orderConfirmResult.donationPrice() > 0) {
                if (donationPrice == null) {
                    log.warn("기부 스톡 가격 정보를 찾을 수 없습니다. 기부 스톡 발급을 건너뜁니다.");
                } else {
                    // 현재 기부 스톡 보유량 조회
                    MemberStockInfo donationStock = memberStockInfoMapper.findPersonalStockInfoById(memberId, DONATION_STOCK_ID);

                    // stock_issue에 발급 기록 저장
                    ecoStockIssueMapper.insert(memberId, DONATION_STOCK_ID);

                    // MEMBER_STOCK_INFO 업데이트
                    if (donationStock.memberStockInfoId() == null) {
                        // 신규 생성
                        memberStockInfoMapper.insertMemberStockInfo(memberId, DONATION_STOCK_ID, 1, donationPrice.getStockPrice().intValue());
                    } else {
                        // 기존 업데이트
                        int newQuantity = donationStock.currentTotalQuantity() + 1;
                        long newAmount = donationStock.currentTotalAmount() + donationPrice.getStockPrice();
                        memberStockInfoMapper.updateMemberStockInfo(donationStock.memberStockInfoId(), newQuantity, newAmount);
                    }

                    totalIssuedCount++;
                    totalStockValue += donationPrice.getStockPrice().intValue();
                    log.info("기부 스톡 발급 완료 - memberId: {}, donationAmount: {}, stockPrice: {}",
                        memberId, orderConfirmResult.donationPrice(), donationPrice.getStockPrice());
                }
            }
        } catch (Exception e) {
            log.error("에코스톡 발급 실패 - orderHistoryId: {}, memberId: {}", orderConfirmResult.orderHistoryId(), memberId, e);
            throw new OrderException(OrderError.ECOSTOCK_ISSUE_FAILED);
        }

        // 발급 후 총 보유량 조회 (친환경 + 기부 스톡 합계)
        MemberStockInfo finalEcoStock = memberStockInfoMapper.findPersonalStockInfoById(memberId, ECO_PRODUCT_STOCK_ID);
        MemberStockInfo finalDonationStock = memberStockInfoMapper.findPersonalStockInfoById(memberId, DONATION_STOCK_ID);

        int newTotalStockCount = 0;
        int newTotalPurchaseAmount = 0;

        if (finalEcoStock != null && finalEcoStock.memberStockInfoId() != null) {
            newTotalStockCount += finalEcoStock.currentTotalQuantity();
            newTotalPurchaseAmount += finalEcoStock.currentTotalAmount();
        }

        if (finalDonationStock != null && finalDonationStock.memberStockInfoId() != null) {
            newTotalStockCount += finalDonationStock.currentTotalQuantity();
            newTotalPurchaseAmount += finalDonationStock.currentTotalAmount();
        }

        return EcoStockIssueResponse.builder()
            .orderHistoryId(orderConfirmResult.orderHistoryId())
            .orderNumber(orderConfirmResult.orderNumber())
            .memberId(memberId)
            .issuedStockCount(totalIssuedCount)
            .issuedStockValue(totalStockValue)
            .totalStockCount(newTotalStockCount)
            .totalPurchaseAmount(newTotalPurchaseAmount)
            .confirmedAt(LocalDateTime.now())
            .message("구매 확정 에코스톡 발급 완료")
            .build();

    }

}
