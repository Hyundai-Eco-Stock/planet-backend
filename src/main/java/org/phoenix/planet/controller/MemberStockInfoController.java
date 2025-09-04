package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;
import org.phoenix.planet.dto.eco_stock_info.response.MemberStockInfoWithDetail;
import org.phoenix.planet.service.eco_stock.EcoStockService;
import org.phoenix.planet.service.eco_stock.MemberStockInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/portfolio")
public class MemberStockInfoController {

    private final MemberStockInfoService memberStockInfoService;
    private final EcoStockService ecoStockService;

    @PostMapping("/stock/sell")
    public ResponseEntity<?> sellEcoStock(@RequestBody SellStockRequest sellStockRequest,
            @LoginMemberId Long loginMemberId) {

        log.info("sell stock request:{} {}", sellStockRequest, loginMemberId);

        ecoStockService.sellStock(loginMemberId, sellStockRequest);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getUserInfo(@RequestParam("ecoStockId") Long ecoStockId,
            @LoginMemberId Long loginMemberId) {

        log.info("get user info:{} {}", ecoStockId, loginMemberId);

        MemberStockInfo memberStockInfo = memberStockInfoService.findPersonalStockInfoById(
                loginMemberId, ecoStockId);

        log.info("get user info:{}", memberStockInfo);

        return ResponseEntity.ok().body(memberStockInfo);
    }

    @GetMapping("/summaries")
    public ResponseEntity<?> getUserInfoAll(@LoginMemberId Long loginMemberId) {

        log.info("get user info: {}", loginMemberId);

        List<MemberStockInfoWithDetail> memberStockInfoWithDetails =
                memberStockInfoService.findAllPersonalStockInfoByMemberId(loginMemberId);

        log.info("get user info:{}", memberStockInfoWithDetails);

        return ResponseEntity.ok().body(memberStockInfoWithDetails);
    }

    /* 마이페이지 - 모든 종목 가격 */
    @GetMapping("/price-list")
    public ResponseEntity<?> getAllEcosStockPrice() {
        return ResponseEntity.ok().body(memberStockInfoService.getAllEcosStockPrice());
    }
}
