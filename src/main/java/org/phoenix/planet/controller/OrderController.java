package org.phoenix.planet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.order.request.CreateOrderRequest;
import org.phoenix.planet.dto.order.response.CreateOrderResponse;
import org.phoenix.planet.dto.order.response.OrderDraftResponse;
import org.phoenix.planet.service.order.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @LoginMemberId Long memberId
    ) {
        CreateOrderResponse response = orderService.createOrder(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{order-number}")
    public ResponseEntity<OrderDraftResponse> getOrderDraft(
            @PathVariable("order-number") String orderNumber,
            @LoginMemberId Long memberId
    ) {
        OrderDraftResponse response = orderService.getOrderDraft(orderNumber, memberId);
        return ResponseEntity.ok(response);
    }

}
