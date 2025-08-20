package org.phoenix.planet.service.receipt;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.KafkaTopic;
import org.phoenix.planet.dto.eco_stock_certificate.request.PaperBagNoUseCertificateRequest;
import org.phoenix.planet.dto.eco_stock_certificate.request.TumblerCertificateRequest;
import org.phoenix.planet.dto.offline.raw.KafkaOfflinePayInfo;
import org.phoenix.planet.dto.offline.raw.OfflinePayHistory;
import org.phoenix.planet.dto.offline.raw.OfflinePayProductSaveRequest;
import org.phoenix.planet.dto.offline.raw.OfflinePaySaveRequest;
import org.phoenix.planet.dto.offline.raw.OfflineProduct;
import org.phoenix.planet.dto.offline.request.OfflinePayload;
import org.phoenix.planet.dto.offline.request.OfflinePayload.Item;
import org.phoenix.planet.producer.ReceiptEventProducer;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
import org.phoenix.planet.service.offline.OfflinePayHistoryService;
import org.phoenix.planet.service.offline.OfflinePayProductService;
import org.phoenix.planet.service.offline.OfflineProductService;
import org.phoenix.planet.util.receipt.ReceiptNoGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflinePayServiceImpl implements OfflinePayService {

    private final ReceiptEventProducer receiptEventProducer;
    private final OfflinePayHistoryService offlinePayHistoryService;
    private final OfflinePayProductService offlinePayProductService;
    private final OfflineProductService offlineProductService;
    private final ReceiptNoGenerator receiptNoGenerator;
    private final EcoStockIssueService ecoStockIssueService;

    @Override
    @Transactional
    public void save(OfflinePayload payload) {
        // 오프라인 결제 정보 저장
        OfflinePaySaveRequest offlinePaySaveRequest = OfflinePaySaveRequest.builder()
            .shopId(payload.shopId())
            .cardCompanyId(payload.cardCompanyId())
            .cardNumberLast4(payload.last4())
            .totalPrice(offlineProductService.getTotalPriceByIds(
                payload.items().stream()
                    .map(Item::productId)
                    .toList()))
            .barcode(receiptNoGenerator.generate(
                payload.shopId(),
                payload.posId(),
                payload.dailySeq(),
                LocalDateTime.now()))
            .build();
        long offlinePayHistoryId = offlinePayHistoryService.save(offlinePaySaveRequest);

        // 결제 상품 정보들 저장
        payload.items()
            .forEach(item -> {
                OfflineProduct offlineProduct = offlineProductService.searchById(item.productId());

                offlinePayProductService.save(
                    OfflinePayProductSaveRequest.builder()
                        .offlinePayHistoryId(offlinePayHistoryId)
                        .productId(item.productId())
                        .name(offlineProduct.name())
                        .price(offlineProduct.price())
                        .amount(item.amount())
                        .build());
            });

        receiptEventProducer.publish(
            KafkaTopic.OFFLINE_PAY_DETECTED,
            KafkaOfflinePayInfo.builder()
                .offlinePayHistoryId(offlinePayHistoryId)
                .posId(payload.posId())
                .dailySeq(payload.dailySeq())
                .shopId(payload.shopId())
                .cardCompanyId(payload.cardCompanyId())
                .cardNumber(payload.cardNumber())
                .last4(payload.last4())
                .items(payload.items())
                .summary(payload.summary())
                .build());
    }

    @Override
    @Transactional
    public void certificateTumbler(long loginMemberId,
        TumblerCertificateRequest tumblerCertificateRequest) {
        // 결제 내역 조회
        OfflinePayHistory offlinePayHistory = offlinePayHistoryService.searchByBarcode(
            tumblerCertificateRequest.code());
        // TODO: 결제 상점 조회
        // TODO: 결제 상점 타입이 CAFE가 아니면
        // throw new IllegalArgumentException("카페에서 결제한 영수증 내역이 아닙니다");

        // TODO: 결제한 상품 중에 "텀블러 할인" 이 없으면
        // throw new IllegalArgumentException("텀블러 할인 내역이 없는 영수증 내역입니다");
        if (offlinePayHistory.stockIssued()) {
            throw new IllegalArgumentException("이미 발급된 영수증 내역입니다");
        }
        // 에코 스톡 발급 처리
        offlinePayHistoryService.updateStockIssueStatusTrue(
            offlinePayHistory.offlinePayHistoryId());
        // 텀블러 에코스톡 발급 (일단 하나만 발급)
        ecoStockIssueService.publish(
            loginMemberId,
            1L,
            1
        );
    }

    @Override
    @Transactional
    public void certificatePaperBagNoUse(long loginMemberId,
        PaperBagNoUseCertificateRequest paperBagNoUseCertificateRequest) {
        // 결제 내역 조회
        OfflinePayHistory offlinePayHistory = offlinePayHistoryService.searchByBarcode(
            paperBagNoUseCertificateRequest.code());
        // TODO: 결제 상점 조회
        // TODO: 결제 상점 타입이 DEPARTMENT_STORE, FOOD_MALL이 아니면
        // throw new IllegalArgumentException("백화점에서 결제한 영수증 내역이 아닙니다");

        // TODO: 결제 상품 조회

        // TODO: 결제한 상품 중에 "종이백" 이 있으면
        //  throw new IllegalArgumentException("종이백을 구매한 영수증 내역입니다");
        if (offlinePayHistory.stockIssued()) {
            throw new IllegalArgumentException("이미 발급된 영수증 내역입니다");
        }
        // 에코 스톡 발급 처리
        offlinePayHistoryService.updateStockIssueStatusTrue(
            offlinePayHistory.offlinePayHistoryId());

        // 종이백 미사용 에코스톡 발급 (일단 하나만 발급)
        ecoStockIssueService.publish(
            loginMemberId,
            4L,
            1
        );
    }
}
