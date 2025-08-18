package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.dto.member.response.MemberListResponse;
import org.phoenix.planet.service.member.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<MemberListResponse>> searchAllMembers() {

        List<MemberListResponse> memberList = memberService.searchAllMembers();
        return ResponseEntity.ok(memberList);
    }
}
