package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.admin.donation.DonationAmountsByDayResponse;
import org.phoenix.planet.dto.admin.donation.DonatorPercentageResponse;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockHoldingAmountGroupByMemberResponse;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockIssuePercentageResponse;
import org.phoenix.planet.dto.admin.order_product.ProductOrderDataGroupByCategoryResponse;
import org.phoenix.planet.dto.admin.order_product.ProductOrderDataGroupByDayResponse;
import org.phoenix.planet.dto.admin.phti.IssueAndOrderPatternsByPhtiResponse;
import org.phoenix.planet.dto.admin.phti.MemberPercentageByPhtiResponse;
import org.phoenix.planet.dto.admin.raffle.RaffleParticipationByDayResponse;
import org.phoenix.planet.dto.admin.raffle.RaffleParticipationResponse;
import org.phoenix.planet.service.admin.AdminService;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final FcmService fcmService;
    private final MemberDeviceTokenService memberDeviceTokenService;

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

    /**
     * 일별 주문건수/매출 데이터 + 총 주문 건수 + 총 매출액 + 평균 주문 금액
     *
     * @return
     */
    @GetMapping("/product-orders-group-by-day")
    public ResponseEntity<ProductOrderDataGroupByDayResponse> fetchProductOrderDataGroupByDay() {

        ProductOrderDataGroupByDayResponse response = adminService.fetchProductOrderDataGroupByDay();
        return ResponseEntity.ok(response);
    }

    /**
     * 카테고리별 판매 비율 데이터 + Top 카테고리
     *
     * @return
     */
    @GetMapping("/product-orders-group-by-category")
    public ResponseEntity<ProductOrderDataGroupByCategoryResponse> fetchProductOrderDataGroupByCategory() {

        ProductOrderDataGroupByCategoryResponse response = adminService.fetchProductOrderDataGroupByCategory();
        return ResponseEntity.ok(response);
    }

    /**
     * PHTI 유형별 사용자 분포 데이터 + 설문참여한 총 사용자 + 최다 사용자 PHTI
     *
     * @return
     */
    @GetMapping("/member-percentage-by-phti")
    public ResponseEntity<MemberPercentageByPhtiResponse> fetchMemberPercentageByPhti() {

        MemberPercentageByPhtiResponse response = adminService.fetchMemberPercentageByPhti();
        return ResponseEntity.ok(response);
    }

    /**
     * PHTI 유형별 주문/교환 패턴 데이터 + 평균 주문 건수
     *
     * @return
     */
    @GetMapping("/issue-and-order-patterns-by-phti")
    public ResponseEntity<IssueAndOrderPatternsByPhtiResponse> fetchIssueAndOrderPatternsByPhti() {

        IssueAndOrderPatternsByPhtiResponse response = adminService.fetchIssueAndOrderPatternsByPhti();
        return ResponseEntity.ok(response);
    }

    /**
     * 날짜별 총 기부 금액 추이 데이터 + 총 기부 금액
     *
     * @return
     */
    @GetMapping("/donation-amounts-by-day")
    public ResponseEntity<DonationAmountsByDayResponse> fetchDonationAmountsByDay() {

        DonationAmountsByDayResponse response = adminService.fetchDonationAmountsByDay();
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자별 기부 금액 참여율 + 참여 사용자 수 + 참여율
     *
     * @return
     */
    @GetMapping("/donator-percentage")
    public ResponseEntity<DonatorPercentageResponse> fetchDonatorPercentage() {

        DonatorPercentageResponse response = adminService.fetchDonatorPercentage();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/raffles-participation-by-raffle")
    public ResponseEntity<RaffleParticipationResponse> fetchParticipationByRaffle() {

        RaffleParticipationResponse response = adminService.fetchRaffleParticipationByRaffle();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/raffle-participation-by-day")
    public ResponseEntity<RaffleParticipationByDayResponse> fetchParticipationByDay() {

        RaffleParticipationByDayResponse response = adminService.fetchRaffleParticipationByDay();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/notification/test")
    public ResponseEntity<Void> testNotification() {

        List<String> tokens = memberDeviceTokenService.findAll();
        fcmService.sendCustomNotification(tokens, "Test", "test 메시지 입니다.", "/login");
        return ResponseEntity.ok().build();
    }

    /* 7일간 주문량 */
    @GetMapping("/7days-order-count")
    public ResponseEntity<?> orderCountFor7Days() {

        return ResponseEntity.ok(adminService.fetch7DaysOrderCount());
    }

    /* 카테고리별 판매량 */
    @GetMapping("/category-sales")
    public ResponseEntity<?> categorySales() {

        return ResponseEntity.ok(adminService.fetchCategorySales());
    }

}
