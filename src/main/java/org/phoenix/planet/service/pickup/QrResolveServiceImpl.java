package org.phoenix.planet.service.pickup;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.constant.OrderError;
import org.phoenix.planet.dto.order.raw.OrderConfirmResult;
import org.phoenix.planet.dto.pickup.raw.OrderQrHeader;
import org.phoenix.planet.dto.pickup.raw.OrderQrInfo;
import org.phoenix.planet.dto.pickup.raw.ProductQrInfo;
import org.phoenix.planet.error.order.OrderException;
import org.phoenix.planet.mapper.OrderHistoryMapper;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
import org.phoenix.planet.util.qr.QrCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QrResolveServiceImpl implements QrResolveService {

    private final OrderHistoryMapper orderHistoryMapper;
    private final EcoStockIssueService ecoStockIssueService;

    @Value("${qr.secret-key:default-qr-secret-key-2024}")
    private String qrSecretKey;

    @Value("${qr.allowed-skew-seconds:86400}")
    private long allowedSkewSeconds;

    @Override
    @Transactional
    public OrderQrInfo resolve(String d) {
        // QR 파싱/검증
        final QrCodec.Parsed parsed = QrCodec.parseAndVerify(d, qrSecretKey, allowedSkewSeconds);
        final Long orderHistoryId = parsed.orderId;

        // DB 조회
        OrderQrHeader header = orderHistoryMapper.selectOrderQrHeader(orderHistoryId);
        if (header == null) {
            throw new OrderException(OrderError.ORDER_NOT_FOUND);
        }
        List<ProductQrInfo> products = orderHistoryMapper.selectOrderProducts(orderHistoryId);
        OrderConfirmResult orderConfirmResult = OrderConfirmResult.builder()
            .orderHistoryId(orderHistoryId)
            .orderNumber(header.orderNumber())
            .donationPrice(header.donationPrice())
            .confirmedAt(LocalDateTime.now())
            .build();

        ecoStockIssueService.issueEcoStock(orderConfirmResult, header.memberId());

        return new OrderQrInfo(
            header.orderId(),
            header.orderNumber(),
            header.storeId(),
            header.storeName(),
            products,
            header.totalAmount()
        );

    }

}