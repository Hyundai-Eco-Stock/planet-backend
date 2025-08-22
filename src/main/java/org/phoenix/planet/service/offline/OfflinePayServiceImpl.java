package org.phoenix.planet.service.offline;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock_certificate.request.PaperBagNoUseCertificateRequest;
import org.phoenix.planet.dto.eco_stock_certificate.request.TumblerCertificateRequest;
import org.phoenix.planet.dto.offline.raw.OfflinePayHistory;
import org.phoenix.planet.dto.offline.raw.OfflinePayProduct;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflinePayServiceImpl implements OfflinePayService {

    // 오프라인 결제, 상품 관련
    private final OfflinePayHistoryService offlinePayHistoryService;
    private final OfflinePayProductService offlinePayProductService;
    private final OfflineProductService offlineProductService;
    private final OfflineShopService offlineShopService;
    // 에코 스톡
    private final EcoStockIssueService ecoStockIssueService;

    @Override
    @Transactional
    public void certificateTumbler(long loginMemberId,
        TumblerCertificateRequest tumblerCertificateRequest) {
        // 결제 내역 조회
        OfflinePayHistory offlinePayHistory = offlinePayHistoryService.searchByBarcode(
            tumblerCertificateRequest.code());
        if (offlinePayHistory.stockIssued()) {
            throw new IllegalArgumentException("이미 텀블러 에코스톡이 발급된 영수증 내역입니다");
        }

        // 결제 상점 조회
        String shopType = offlineShopService.searchTypeById(offlinePayHistory.shopId());
        if (!"CAFE".equals(shopType)) {
            throw new IllegalArgumentException("카페에서 결제한 영수증 내역이 아닙니다");
        }

        // 결제한 상품 조회
        List<OfflinePayProduct> productList = offlinePayProductService.searchByPayHistoryId(
            offlinePayHistory.offlinePayHistoryId());
        boolean hasTumblerDiscount = productList.stream()
            .anyMatch(p -> "텀블러 할인".equals(p.name()));
        if (!hasTumblerDiscount) {
            throw new IllegalArgumentException("텀블러 할인 내역이 없는 영수증 내역입니다");
        }

        // 에코 스톡 발급 처리
        offlinePayHistoryService.updateStockIssueStatusTrue(
            offlinePayHistory.offlinePayHistoryId());
        // 텀블러 에코스톡 발급 (일단 하나만 발급)
        ecoStockIssueService.issueStock(
            loginMemberId,
            1L,
            1
        );
        log.info("텀블러 사용 에코스톡 발급 완료");
    }

    @Override
    @Transactional
    public void certificatePaperBagNoUse(long loginMemberId,
        PaperBagNoUseCertificateRequest paperBagNoUseCertificateRequest) {
        // 결제 내역 조회
        OfflinePayHistory offlinePayHistory = offlinePayHistoryService.searchByBarcode(
            paperBagNoUseCertificateRequest.code());
        if (offlinePayHistory.stockIssued()) {
            throw new IllegalArgumentException("이미 종이백 미사용 에코스톡이 발급된 영수증 내역입니다");
        }

        // 결제 상점 조회
        String shopType = offlineShopService.searchTypeById(offlinePayHistory.shopId());
        if (!"DEPARTMENT_STORE".equals(shopType) && !"FOOD_MALL".equals(shopType)) {
            throw new IllegalArgumentException("백화점 내에서 결제한 영수증 내역이 아닙니다");
        }

        // 결제한 상품 조회
        List<OfflinePayProduct> productList = offlinePayProductService.searchByPayHistoryId(
            offlinePayHistory.offlinePayHistoryId());
        boolean hasPaperBag = productList.stream()
            .anyMatch(p -> "종이백".equals(p.name()));
        if (hasPaperBag) {
            throw new IllegalArgumentException("종이백을 구매한 영수증 내역입니다");
        }

        // 에코 스톡 발급 처리
        offlinePayHistoryService.updateStockIssueStatusTrue(
            offlinePayHistory.offlinePayHistoryId());
        // 종이백 미사용 에코스톡 발급 (일단 하나만 발급)
        ecoStockIssueService.issueStock(
            loginMemberId,
            4L,
            1
        );
        log.info("종이백 미사용 에코스톡 발급 완료");
    }
}
