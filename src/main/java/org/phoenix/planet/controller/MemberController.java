package org.phoenix.planet.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.car.request.CarRegisterRequest;
import org.phoenix.planet.dto.car.response.MemberCarResponse;
import org.phoenix.planet.dto.member.response.MemberListResponse;
import org.phoenix.planet.service.car.MemberCarService;
import org.phoenix.planet.service.member.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/me/cars")
    public ResponseEntity<MemberCarResponse> searchMyCar(
        @LoginMemberId long loginMemberId
    ) {

        MemberCarResponse carInfo = memberCarService.searchCarByMemberId(loginMemberId);
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
}
