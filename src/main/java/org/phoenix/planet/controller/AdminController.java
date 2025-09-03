package org.phoenix.planet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockHoldingAmountGroupByMemberResponse;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockIssuePercentageResponse;
import org.phoenix.planet.service.admin.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 에코스톡 발급 비율 데이터 + 총 발급량 + 에코스톡 종류 수 +
     *
     * @return
     */
    @GetMapping("/eco-stock-issue-percentage")
    public ResponseEntity<EcoStockIssuePercentageResponse> fetchEcoStockIssuePercentageData() {

        EcoStockIssuePercentageResponse response = adminService.fetchEcoStockIssuePercentageData();
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자별 에코스톡 보유 현황 데이터 + 총 사용자 + 평균 보유량
     *
     * @return
     */
    @GetMapping("/eco-stock-issue-holdings")
    public ResponseEntity<EcoStockHoldingAmountGroupByMemberResponse> fetchEcoStockHoldingAmountDataGroupByMember() {

        EcoStockHoldingAmountGroupByMemberResponse response = adminService.fetchEcoStockHoldingAmountDataGroupByMember();
        return ResponseEntity.ok(response);
    }

}
