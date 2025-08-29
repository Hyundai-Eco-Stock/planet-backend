package org.phoenix.planet.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.payment.request.PaymentCancelReqeust;
import org.phoenix.planet.dto.payment.request.PaymentConfirmRequest;
import org.phoenix.planet.dto.payment.response.PaymentCancelResponse;
import org.phoenix.planet.dto.payment.response.PaymentConfirmResponse;
import org.phoenix.planet.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        PaymentConfirmResponse response = paymentService.confirmPayment(request, memberId);

        log.info("결제 승인 API 성공: orderId={}, memberId={}", request.orderId(), memberId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{order-id}/cancel")
    public ResponseEntity<PaymentCancelResponse> cancelOrder(
        @PathVariable("order-id") Long orderHistoryId,
        @RequestBody PaymentCancelReqeust request
    ) {
        log.info("주문 전체 취소 요청 - orderId: {}, reason: {}", orderHistoryId, request.cancelReason());

        PaymentCancelResponse response = paymentService.cancelEntireOrder(orderHistoryId, request);
        log.info("주문 전체 취소 완료 - orderId: {}, cancelAmount: {}", orderHistoryId, response.cancelAmount());

        return ResponseEntity.ok(response);
    }

}
