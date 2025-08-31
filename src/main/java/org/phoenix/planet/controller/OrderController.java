package org.phoenix.planet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.order.request.CreateOrderRequest;
import org.phoenix.planet.dto.order.response.CreateOrderResponse;
import org.phoenix.planet.dto.order.response.OrderDraftResponse;
import org.phoenix.planet.dto.order.response.EcoStockIssueResponse;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
import org.phoenix.planet.service.order.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final EcoStockIssueService ecoStockIssueService;

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

    @PostMapping("{order-id}/confirm")
    public ResponseEntity<EcoStockIssueResponse> confirmPurchase(
            @PathVariable("order-id") Long orderHistoryId,
            @LoginMemberId Long memberId
    ) {
        // 구매확정 + 에코스톡 발급을 하나의 트랜잭션으로 처리
        EcoStockIssueResponse response = orderService.confirmPurchaseAndIssueEcoStock(orderHistoryId, memberId);

        return ResponseEntity.ok(response);
    }

}
