package org.phoenix.planet.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.car.request.CarRegisterRequest;
import org.phoenix.planet.dto.car.response.MemberCarResponse;
import org.phoenix.planet.dto.member.request.ProfileUpdateRequest;
import org.phoenix.planet.dto.member.response.MemberListResponse;
import org.phoenix.planet.dto.member.response.MemberPointWithHistoriesResponse;
import org.phoenix.planet.dto.member.response.MemberProfileResponse;
import org.phoenix.planet.dto.member.response.MyEcoDealResponse;
import org.phoenix.planet.dto.member.response.MyOrderResponse;
import org.phoenix.planet.dto.member.response.MyRaffleResponse;
import org.phoenix.planet.dto.member_card.MemberCardRegisterRequest;
import org.phoenix.planet.dto.member_card.MemberCardsInfoResponse;
import org.phoenix.planet.dto.phti.raw.PhtiResultResponse;
import org.phoenix.planet.service.car.MemberCarService;
import org.phoenix.planet.service.card.MemberCardService;
import org.phoenix.planet.service.member.MemberService;
import org.phoenix.planet.service.phti.PhtiService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberCarService memberCarService;
    private final MemberCardService memberCardService;
    private final PhtiService phtiService;

    @GetMapping
    public ResponseEntity<List<MemberListResponse>> searchAllMembers() {

        List<MemberListResponse> memberList = memberService.searchAllMembers();
        return ResponseEntity.ok(memberList);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponse> fetchProfile(
        @LoginMemberId long loginMemberId
    ) {

        MemberProfileResponse memberProfileResponse = memberService.searchProfile(loginMemberId);
        return ResponseEntity.ok(memberProfileResponse);
    }

    @PutMapping(
        value = "/me",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfile(
        @LoginMemberId long loginMemberId,
        @RequestPart("updateProfile") @Valid ProfileUpdateRequest profileUpdateRequest,
        @RequestPart(value = "profileImageFile", required = false) MultipartFile profileImageFile
    ) {

        memberService.updateMemberInfo(loginMemberId, profileUpdateRequest, profileImageFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/cars")
    public ResponseEntity<MemberCarResponse> searchMyCar(
        @LoginMemberId long loginMemberId
    ) {

        MemberCarResponse carInfo = memberCarService.searchByMemberId(loginMemberId);
        return ResponseEntity.ok(carInfo);
    }

    @PostMapping("/me/cars")
    public ResponseEntity<Void> registerMyCar(
        @LoginMemberId long loginMemberId,
        @RequestBody @Valid CarRegisterRequest carRegisterRequest
    ) {

        memberCarService.registerCar(loginMemberId, carRegisterRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/cars")
    public ResponseEntity<Void> unregisterMyCar(@LoginMemberId long loginMemberId) {

        memberCarService.unregisterCar(loginMemberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/orders")
    public ResponseEntity<List<MyOrderResponse>> getMyOrders(
        @LoginMemberId Long memberId
    ) {

        List<MyOrderResponse> list = memberService.getMyOrders(memberId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/me/eco-deals")
    public ResponseEntity<List<MyEcoDealResponse>> getMyEcoDeals(
        @LoginMemberId Long memberId
    ) {

        return ResponseEntity.ok(memberService.getMyEcoDeals(memberId));
    }

    @GetMapping("/me/raffles")
    public ResponseEntity<List<MyRaffleResponse>> getMyRaffles(
        @LoginMemberId Long memberId
    ) {

        System.out.println("여기까지 와요");
        return ResponseEntity.ok(memberService.getMyRaffles(memberId));
    }

    @GetMapping("/me/cards")
    public ResponseEntity<MemberCardsInfoResponse> fetchMyCardInfos(
        @LoginMemberId long loginMemberId
    ) {

        MemberCardsInfoResponse response = memberCardService.getInfoByMemberId(loginMemberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/cards")
    public ResponseEntity<Void> registerMyCardInfo(
        @LoginMemberId long loginMemberId,
        @RequestBody @Valid MemberCardRegisterRequest request
    ) {

        memberCardService.registerCardInfo(loginMemberId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/cards/{member-card-id}")
    public ResponseEntity<Void> deleteMyCardInfo(
        @LoginMemberId long loginMemberId,
        @PathVariable("member-card-id") long memberCardId
    ) {

        memberCardService.deleteCardInfo(loginMemberId, memberCardId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/point-histories")
    public ResponseEntity<MemberPointWithHistoriesResponse> fetchPointHistories(
        @LoginMemberId long loginMemberId
    ) {

        MemberPointWithHistoriesResponse response = memberService.fetchPointHistories(
            loginMemberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/phti-result")
    public ResponseEntity<PhtiResultResponse> fetchMemberPhtiResult(
        @LoginMemberId long loginMemberId
    ) {

        PhtiResultResponse response = phtiService.fetchMemberPhtiResult(loginMemberId);
        return ResponseEntity.ok(response);
    }
}
