package org.phoenix.planet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.order.raw.OrderConfirmResult;
import org.phoenix.planet.dto.order.request.CreateOrderRequest;
import org.phoenix.planet.dto.order.response.CreateOrderResponse;
import org.phoenix.planet.dto.order.response.OrderDraftResponse;
import org.phoenix.planet.dto.order.response.EcoStockIssueResponse;
import org.phoenix.planet.service.eco_stock.EcoStockIssueService;
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
        // 구매확정 처리
        OrderConfirmResult result = orderService.confirmPurchase(orderHistoryId, memberId);

        // 에코스톡 발급
        EcoStockIssueResponse response = ecoStockIssueService.issueEcoStock(result, memberId);

        return ResponseEntity.ok(response);
    }

}
