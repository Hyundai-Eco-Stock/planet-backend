package org.phoenix.planet.controller;

import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/tests")
public class TestController {

    @GetMapping
    public ResponseEntity<?> memberTest(
        @LoginMemberId long loginMemberId
    ) {

        log.info("[memberTest] Login member id: {}", loginMemberId);
        return ResponseEntity.ok("TEST OK");
    }
}
