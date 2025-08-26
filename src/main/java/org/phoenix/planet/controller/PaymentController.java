package org.phoenix.planet.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.constant.PaymentError;
import org.phoenix.planet.dto.payment.request.PaymentConfirmRequest;
import org.phoenix.planet.dto.payment.response.PaymentConfirmResponse;
import org.phoenix.planet.error.payment.PaymentException;
import org.phoenix.planet.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(
            @Valid @RequestBody PaymentConfirmRequest request,
            @LoginMemberId Long memberId
    ) {
        log.info("결제 승인 API 호출: orderId={}, paymentKey={}, memberId={}", request.orderId(), request.paymentKey(), memberId);

        // 필수 파라미터 검증
        validateRequiredFields(request);

        PaymentConfirmResponse response = paymentService.confirmPayment(request, memberId);

        log.info("결제 승인 API 성공: orderId={}, memberId={}", request.orderId(), memberId);
        return ResponseEntity.ok(response);
    }

    /**
     * 필수 필드 검증
     */
    private void validateRequiredFields(PaymentConfirmRequest request) {
        if (request.paymentKey() == null || request.paymentKey().trim().isEmpty()) {
            throw new PaymentException(PaymentError.INVALID_PAYMENT_KEY);
        }

        if (request.orderId() == null || request.orderId().trim().isEmpty()) {
            throw new PaymentException(PaymentError.INVALID_ORDER_ID);
        }

        if (request.amount() == null || request.amount() <= 0) {
            throw new PaymentException(PaymentError.INVALID_PRODUCT_PRICE);
        }

        if (request.products() == null || request.products().isEmpty()) {
            throw new PaymentException(PaymentError.INVALID_PRODUCT_QUANTITY);
        }

        if (request.orderType() == null || request.orderType().toString().trim().isEmpty()) {
            throw new PaymentException(PaymentError.INVALID_ORDER_TYPE);
        }

        // 주문 타입별 추가 검증
        if ("DELIVERY".equals(request.orderType().getValue()) && request.deliveryInfo() == null) {
            throw new PaymentException(PaymentError.DELIVERY_INFO_REQUIRED);
        }

        if ("PICKUP".equals(request.orderType().getValue()) && request.pickupInfo() == null) {
            throw new PaymentException(PaymentError.PICKUP_INFO_REQUIRED);
        }

        // 상품별 검증
        for (PaymentConfirmRequest.OrderProductInfo product : request.products()) {
            if (product.productId() == null) {
                throw new PaymentException(PaymentError.INVALID_ORDER_ID);
            }
            if (product.quantity() == null || product.quantity() <= 0) {
                throw new PaymentException(PaymentError.INVALID_PRODUCT_QUANTITY);
            }
            if (product.price() == null || product.price() <= 0) {
                throw new PaymentException(PaymentError.INVALID_PRODUCT_PRICE);
            }
        }
    }

}
