package org.phoenix.planet.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.car.request.CarRegisterRequest;
import org.phoenix.planet.dto.car.response.MemberCarResponse;
import org.phoenix.planet.dto.member.request.ProfileUpdateRequest;
import org.phoenix.planet.dto.member.response.MemberListResponse;
import org.phoenix.planet.dto.member.response.MemberProfileResponse;
import org.phoenix.planet.dto.order.response.MyEcoDealResponse;
import org.phoenix.planet.dto.order.response.MyOrderResponse;
import org.phoenix.planet.service.car.MemberCarService;
import org.phoenix.planet.service.member.MemberService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberCarService memberCarService;

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

    @GetMapping("/me/orders")
    public ResponseEntity<List<MyOrderResponse>> getMyOrders(
            @LoginMemberId Long memberId
    ) {
        List<MyOrderResponse> list = memberService.getMyOrders(memberId);
        return ResponseEntity.ok(memberService.getMyOrders(memberId));
    }

    @GetMapping("/me/eco-deals")
    public ResponseEntity<List<MyEcoDealResponse>> getMyEcoDeals(
            @LoginMemberId Long memberId
    ) {
        return ResponseEntity.ok(memberService.getMyEcoDeals(memberId));
    }
}
