package org.phoenix.planet.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.*;
import org.phoenix.planet.dto.order.raw.OrderHistory;
import org.phoenix.planet.dto.order.raw.OrderProduct;
import org.phoenix.planet.dto.payment.raw.PaymentHistory;
import org.phoenix.planet.dto.payment.request.PaymentCancelReqeust;
import org.phoenix.planet.dto.payment.request.PaymentConfirmRequest;
import org.phoenix.planet.dto.payment.response.PaymentCancelResponse;
import org.phoenix.planet.dto.payment.response.PaymentConfirmResponse;
import org.phoenix.planet.dto.payment.response.TossPaymentResponse;
import org.phoenix.planet.error.order.OrderException;
import org.phoenix.planet.error.payment.PaymentException;
import org.phoenix.planet.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    @Override
    @Transactional
    public PaymentCancelResponse cancelEntireOrder(Long orderHistoryId, PaymentCancelReqeust request) {
        log.info("주문 전체 취소 프로세스 시작 - orderId: {}, reason: {}", orderHistoryId, request.cancelReason());

        // 주문 및 결제 정보 조회
        OrderHistory order = orderHistoryMapper.findById(orderHistoryId);
        if (order == null) {
            throw new OrderException(OrderError.ORDER_NOT_FOUND);
        }

        PaymentHistory payment = paymentHistoryMapper.findByOrderHistoryId(orderHistoryId);
        if (payment == null) {
            throw new PaymentException(PaymentError.PAYMENT_NOT_FOUND);
        }

        // 취소 가능 상태 검증
        validateCancelableOrder(order, payment);

        // 주문 상품 목록 조회
        List<OrderProduct> orderProducts = orderProductMapper.findOrderProductsByOrderHistoryId(orderHistoryId);

        // TossPayments 취소 요청
        TossPaymentResponse tossResponse = requestTossCancel(payment, order.getFinalPayPrice().intValue(), request.cancelReason());

        // 재고 복구
        restoreInventory(orderProducts);

        //  포인트 환불
        refundPoints(order);

        // 기부 취소 처리
        Long refundedDonation = cancelDonation(order, true, false);

        // 주문 상태를 전체 취소로 업데이트
        updateOrderStatus(orderHistoryId, OrderStatus.ALL_CANCELLED);

        // 결제 상태 업데이트
        updatePaymentStatus(payment.getPaymentId(), true);

        // 모든 주문 상품을 취소 상태로 변경
        orderProductMapper.updateAllOrderProductsCancelStatus(orderHistoryId, CancelStatus.Y);

        Long cancelAmount = (long) tossResponse.cancels().getFirst().cancelAmount();
        Long refundedPoints = order.getUsedPoint() != null ? order.getUsedPoint() : 0L;

        log.info("주문 전체 취소 완료 - orderId: {}, cancelAmount: {}, refundedPoints: {}", orderHistoryId, cancelAmount, refundedPoints);

        return PaymentCancelResponse.success(
                orderHistoryId,
                order.getOrderNumber(),
                cancelAmount,
                refundedPoints,
                "주문이 성공적으로 취소되었습니다."
        );
    }

    /**
     * 취소 가능 상태 검증
     */
    private void validateCancelableOrder(OrderHistory order, PaymentHistory payment) {
        if (order.getOrderStatus() != OrderStatus.PAID) {
            switch (order.getOrderStatus()) {
                case PENDING:
                    throw new IllegalStateException("결제가 완료되지 않은 주문은 취소할 수 없습니다.");
                case ALL_CANCELLED:
                    throw new IllegalStateException("이미 전체 취소된 주문입니다.");
                case DONE:
                    throw new IllegalStateException("구매 확정된 주문은 취소할 수 없습니다.");
                default:
                    throw new IllegalStateException("취소할 수 없는 주문 상태입니다.");
            }
        }

        // 결제 실패 또는 취소된 결제
        if (payment.getStatus() == PaymentStatus.ABORTED ||
                payment.getStatus() == PaymentStatus.CANCELED) {
            throw new IllegalStateException("결제가 완료되지 않은 주문은 취소할 수 없습니다.");
        }
    }

    /**
     * TossPayments 취소 요청
     */
    private TossPaymentResponse requestTossCancel(PaymentHistory payment, Integer cancelAmount, String cancelReason) {
        return tossPaymentsClient.cancelPayment(payment.getPaymentKey(), cancelAmount, cancelReason);
    }

    /**
     * 재고 복구
     */
    private void restoreInventory(List<OrderProduct> orderProducts) {
        for (OrderProduct orderProduct : orderProducts) {
            if (orderProduct.getCancelStatus() == CancelStatus.N) {
                int result = productMapper.restoreStock(orderProduct.getProductId(), orderProduct.getQuantity());

                if (result == 0) {
                    throw new PaymentException(PaymentError.STOCK_RESTORE_FAILED);
                }

                log.info("재고 복구 완료 - productId: {}, quantity: {}", orderProduct.getProductId(), orderProduct.getQuantity());
            }
        }
    }

    /**
     * 포인트 환불
     */
    private void refundPoints(OrderHistory order) {
        if (order.getUsedPoint() != null && order.getUsedPoint().intValue() > 0) {
            int result = memberMapper.restorePointsByMemberId(
                    order.getMemberId(),
                    order.getUsedPoint().intValue()
            );

            if (result == 0) {
                throw new PaymentException(PaymentError.POINT_REFUND_FAILED);
            }

            log.info("포인트 환불 완료 - memberId: {}, amount: {}", order.getMemberId(), order.getUsedPoint());
        }
    }

    /**
     * 기부금 취소 처리 (전체/부분 취소 공통)
     */
    private Long cancelDonation(OrderHistory order, boolean isFullCancel, boolean cancelDonationForPartial) {
        if (order.getDonationPrice() == null || order.getDonationPrice().intValue() <= 0) {
            return 0L;
        }

        // 전체 취소이거나, 부분 취소에서 기부금 취소를 선택한 경우
        if (isFullCancel || cancelDonationForPartial) {
            Long donationAmount = order.getDonationPrice();

            // order_history의 donation_price를 0으로 업데이트
            int result = orderHistoryMapper.updateDonationPrice(order.getOrderHistoryId(), 0L);
            if (result == 0) {
                throw new PaymentException(PaymentError.DONATION_CANCEL_FAILED);
            }

            log.info("기부금 취소 완료 - orderId: {}, donationAmount: {}", order.getMemberId(), donationAmount);

            return donationAmount; // 환불할 기부금 반환
        }

        return 0L; // 기부금 취소 안 함
    }

    /**
     * 주문 상태 업데이트
     */
    private void updateOrderStatus(Long orderId, OrderStatus status) {
        int result = orderHistoryMapper.updateOrderStatus(orderId, status, LocalDateTime.now());
        if (result == 0) {
            throw new PaymentException(PaymentError.ORDER_STATUS_UPDATE_FAILED);
        }
        log.info("주문 상태 업데이트 완료 - orderId: {}, status: {}", orderId, status);
    }

    /**
     * 결제 상태 업데이트 (전체/부분 취소 공통)
     */
    private void updatePaymentStatus(Long paymentId, boolean isFullCancel) {
        PaymentStatus paymentStatus = isFullCancel ? PaymentStatus.CANCELED : PaymentStatus.PARTIAL_CANCELED;
        int result = paymentHistoryMapper.updatePaymentStatus(paymentId, paymentStatus);

        if (result == 0) {
            throw new PaymentException(PaymentError.PAYMENT_STATUS_UPDATE_FAILED);
        }

        log.info("결제 상태 업데이트 완료 - paymentId: {}, status: {}", paymentId, paymentStatus);
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
