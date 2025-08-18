package org.phoenix.planet.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<?> memberTest(){
        log.info("헬스 체크 오케이");
        return ResponseEntity.ok("TEST OK");
    }
}
