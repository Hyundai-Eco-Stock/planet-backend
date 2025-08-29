package org.phoenix.planet.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.CancelStatus;
import org.phoenix.planet.constant.OrderStatus;
import org.phoenix.planet.constant.OrderType;
import org.phoenix.planet.constant.PaymentError;
import org.phoenix.planet.dto.order.raw.OrderHistory;
import org.phoenix.planet.dto.order.raw.OrderProduct;
import org.phoenix.planet.dto.payment.raw.PaymentHistory;
import org.phoenix.planet.dto.payment.request.PaymentConfirmRequest;
import org.phoenix.planet.dto.payment.response.PaymentConfirmResponse;
import org.phoenix.planet.dto.payment.response.TossPaymentResponse;
import org.phoenix.planet.error.payment.PaymentException;
import org.phoenix.planet.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TossPaymentsClient tossPaymentsClient;
    private final PaymentHistoryMapper paymentHistoryMapper;
    private final OrderHistoryMapper orderHistoryMapper;
    private final OrderProductMapper orderProductMapper;
    private final ProductMapper productMapper;
    private final MemberMapper memberMapper;
    private final QrCodeService qrCodeService;

    @Override
    @Transactional
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request, Long memberId) {
        log.info("결제 승인 프로세스 시작: orderId={}, amount={}, memberId={}", request.orderId(), request.amount(), memberId);

        // TossPayments API 결제 승인 호출
        TossPaymentResponse tossResponse = tossPaymentsClient.confirmPayment(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );

        // 결제 승인 검증
        validatePaymentResponse(tossResponse, request);

        // 주문 생성 및 저장
        Long orderHistoryId = createOrderHistory(request, tossResponse, memberId);

        // 주문 상품 저장
        createOrderProducts(request.products(), orderHistoryId);

        // 결제 내역 저장
        PaymentHistory paymentHistory = PaymentHistory.fromTossResponse(tossResponse, orderHistoryId);
        paymentHistoryMapper.insert(paymentHistory);

        // 재고 차감
        deductInventory(request.products());

        // 포인트 차감 (사용한 경우)
        if (request.pointsUsed() != null && request.pointsUsed() > 0) {
            deductUserPoints(memberId, request.pointsUsed());
        }

        // QR 코드 생성
        String qrCodeUrl = null;
        if ("매장 픽업".equals(request.orderType().getValue())) {
            qrCodeUrl = qrCodeService.generatePickupQRCode(orderHistoryId);
            orderHistoryMapper.updateQRCodeUrl(orderHistoryId, qrCodeUrl);
        }

        // 성공 응답 생성
        PaymentConfirmResponse.PaymentResultData resultData = createSuccessResponse(
                tossResponse, orderHistoryId, qrCodeUrl
        );

        log.info("결제 승인 완료: orderId={}, paymentKey={}", request.orderId(), request.paymentKey());

        return new PaymentConfirmResponse(true, "결제가 성공적으로 완료되었습니다.", resultData);
    }

    /**
     * TossPayments 응답 검증
     */
    private void validatePaymentResponse(TossPaymentResponse response, PaymentConfirmRequest request) {
        if (!"DONE".equals(response.status())) {
            throw new PaymentException(PaymentError.TOSS_PAYMENT_FAILED);
        }

        if (!request.amount().equals(response.totalAmount())) {
            throw new PaymentException(PaymentError.PAYMENT_AMOUNT_MISMATCH);
        }

        if (!request.orderId().equals(response.orderId())) {
            throw new PaymentException(PaymentError.PAYMENT_ORDER_MISMATCH);
        }
    }

    /**
     * 주문 내역 생성
     */
    private Long createOrderHistory(PaymentConfirmRequest request, TossPaymentResponse tossResponse, Long memberId) {
        String orderNumber = request.orderId();

        long totalProductAmount = request.products().stream()
                .mapToLong(product -> calculateDiscountedPrice(product.price(), product.salePercent()) * product.quantity())
                .sum();

        long finalPayPrice = totalProductAmount - (request.pointsUsed() != null ? request.pointsUsed() : 0) +
                (request.donationAmount() != null ? request.donationAmount() : 0);

        Long departmentStoreId = null;
        if ("PICKUP".equals(request.orderType().getValue()) && request.pickupInfo() != null) {
            departmentStoreId = request.pickupInfo().departmentStoreId();
        }

        OrderHistory orderHistory = OrderHistory.builder()
                .orderNumber(orderNumber)
                .orderStatus(OrderStatus.PAID)
                .originPrice(totalProductAmount)
                .usedPoint((long) (request.pointsUsed() != null ? request.pointsUsed() : 0))
                .donationPrice((long) (request.donationAmount() != null ? request.donationAmount() : 0))
                .finalPayPrice(finalPayPrice)
                .ecoDealQrUrl(null)   // QR 코드 생성 후 업데이트
                .memberId(memberId)
                .departmentStoreId(departmentStoreId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        orderHistoryMapper.insert(orderHistory);
        return orderHistory.getOrderHistoryId();
    }

    /**
     * 주문 상품 생성
     */
    private void createOrderProducts(java.util.List<PaymentConfirmRequest.OrderProductInfo> products, Long orderHistoryId) {
        for (PaymentConfirmRequest.OrderProductInfo productInfo : products) {
            long discountedPrice = calculateDiscountedPrice(productInfo.price(), productInfo.salePercent());
            long discountAmount = (productInfo.price() - discountedPrice) * productInfo.quantity();

            OrderProduct orderProduct = OrderProduct.builder()
                    .price(discountedPrice)
                    .quantity(productInfo.quantity())
                    .cancelStatus(CancelStatus.N)
                    .orderType(productInfo.ecoDealStatus() ? OrderType.PICKUP : OrderType.DELIVERY)
                    .discountPrice(discountAmount)
                    .orderHistoryId(orderHistoryId)
                    .productId(productInfo.productId())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            orderProductMapper.insert(orderProduct);
        }
    }

    /**
     * 재고 차감
     */
    private void deductInventory(java.util.List<PaymentConfirmRequest.OrderProductInfo> products) {
        for (PaymentConfirmRequest.OrderProductInfo product : products) {
            int result = productMapper.deductStock(product.productId(), product.quantity());
            if (result == 0) {
                throw new PaymentException(PaymentError.INSUFFICIENT_STOCK);
            }
        }
    }

    /**
     * 사용자 포인트 차감
     */
    private void deductUserPoints(Long memberId, int pointsToDeduct) {
        int result = memberMapper.deductPointsByMemberId(memberId, pointsToDeduct);
        if (result == 0) {
            throw new PaymentException(PaymentError.INSUFFICIENT_POINTS);
        }
    }

    /**
     * 할인 적용된 가격 계산
     */
    private long calculateDiscountedPrice(int originalPrice, int salePercent) {
        if (salePercent <= 0) {
            return originalPrice;
        }
        return Math.round(originalPrice * (100 - salePercent / 100.0));
    }

    private PaymentConfirmResponse.PaymentResultData createSuccessResponse(
            TossPaymentResponse tossResponse, Long orderHistoryId, String qrCodeUrl
    ) {
        String orderNumber = orderHistoryMapper.findOrderNumberById(orderHistoryId);

        return new PaymentConfirmResponse.PaymentResultData(
                tossResponse.orderId(),
                orderNumber,
                tossResponse.paymentKey(),
                tossResponse.totalAmount(),
                tossResponse.method(), // 한글명 그대로
                tossResponse.status(),
                qrCodeUrl, // QR 코드 S3 URL (픽업 주문인 경우만)
                tossResponse.approvedAt() != null ?
                        tossResponse.approvedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null,
                tossResponse.receipt() != null ? tossResponse.receipt().url() : null
        );
    }

}
