package org.phoenix.planet.service.pickup;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.OrderError;
import org.phoenix.planet.constant.OrderStatus;
import org.phoenix.planet.dto.order.raw.OrderConfirmResult;
import org.phoenix.planet.dto.order.raw.OrderHistory;
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

@Slf4j
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

        if (header.orderStatus() != OrderStatus.COMPLETED) {
            OrderConfirmResult orderConfirmResult = OrderConfirmResult.builder()
                .orderHistoryId(orderHistoryId)
                .orderNumber(header.orderNumber())
                .donationPrice(header.donationPrice())
                .confirmedAt(LocalDateTime.now())
                .build();

            ecoStockIssueService.issueEcoStock(orderConfirmResult, header.memberId());
        } else {
            log.info("이미 완료 상태 - orderHistoryId={}", orderHistoryId);
            throw new OrderException(OrderError.ORDER_ALREADY_COMPLETED);
        }

        int updatedRows = orderHistoryMapper.updateOrderStatus(orderHistoryId, OrderStatus.COMPLETED, LocalDateTime.now());
        if (updatedRows == 0) {
            throw new OrderException(OrderError.ORDER_STATUS_UPDATE_FAILED);
        }

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