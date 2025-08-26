package org.phoenix.planet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.raffle.RaffleResponse;
import org.phoenix.planet.service.raffle.RaffleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/raffle")
@Slf4j
@RequiredArgsConstructor
public class RaffleController {
    private final RaffleService raffleService;

    @GetMapping("/list")
    public ResponseEntity<?> getRaffleList() {

        List<RaffleResponse> raffleList = raffleService.findAll();

        log.info("raffleList: {}", raffleList);

        return ResponseEntity.ok(raffleList);
    }
}
