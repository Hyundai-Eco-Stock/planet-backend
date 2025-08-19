package org.phoenix.planet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.fcm.request.FcmTokenRegisterRequest;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/fcm-tokens")
@RequiredArgsConstructor
public class FcmTokenController {

    private final MemberDeviceTokenService memberDeviceTokenService;

    @PostMapping
    public ResponseEntity<Void> registerFcmToken(
        @RequestBody FcmTokenRegisterRequest fcmTokenRegisterRequest,
        @LoginMemberId long loginMemberId
    ) {

        log.info("FCM 등록 요청: {}", fcmTokenRegisterRequest.token());
        memberDeviceTokenService.saveOrUpdate(loginMemberId, fcmTokenRegisterRequest.token());
        return ResponseEntity.ok().build();
    }
}
