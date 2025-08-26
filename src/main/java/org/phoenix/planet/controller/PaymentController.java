package org.phoenix.planet.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.payment.request.PaymentConfirmRequest;
import org.phoenix.planet.dto.payment.response.PaymentConfirmResponse;
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

        PaymentConfirmResponse response = paymentService.confirmPayment(request, memberId);

        log.info("결제 승인 API 성공: orderId={}, memberId={}", request.orderId(), memberId);
        return ResponseEntity.ok(response);
    }

}
