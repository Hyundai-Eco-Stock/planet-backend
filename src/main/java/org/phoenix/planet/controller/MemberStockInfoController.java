package org.phoenix.planet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;
import org.phoenix.planet.service.eco_stock.EcoStockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/portfolio")
public class MemberStockInfoController {

    private final EcoStockService ecoStockService;

    @PostMapping("/stock/sell")
    public ResponseEntity<?> sellEcoStock(@RequestBody SellStockRequest sellStockRequest, @LoginMemberId Long loginMemberId) {

        log.info("sell stock request:{} {}", sellStockRequest, loginMemberId);

        ecoStockService.sellStock(loginMemberId, sellStockRequest);

        return ResponseEntity.ok().build();
    }
}
