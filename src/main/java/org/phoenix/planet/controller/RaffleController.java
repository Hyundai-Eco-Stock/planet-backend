package org.phoenix.planet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.raffle.RaffleDetailResponse;
import org.phoenix.planet.dto.raffle.RaffleResponse;
import org.phoenix.planet.dto.raffle.response.ParticipateRaffleResponse;
import org.phoenix.planet.service.raffle.RaffleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/raffles")
@Slf4j
@RequiredArgsConstructor
public class RaffleController {
    private final RaffleService raffleService;

    @GetMapping
    public ResponseEntity<List<RaffleResponse>> getRaffleList() {

        List<RaffleResponse> raffleList = raffleService.findAll();

        log.info("raffleList: {}", raffleList);

        return ResponseEntity.ok(raffleList);
    }

    @GetMapping("/{raffleId}")
    public ResponseEntity<RaffleDetailResponse> getRaffleDetail(@PathVariable Long raffleId) {

        RaffleDetailResponse raffleDetailResponse = raffleService.findDetailById(raffleId);

        log.info("RaffleDetailResponse: {}", raffleDetailResponse);

        return ResponseEntity.ok(raffleDetailResponse);
    }

    @PostMapping("/{raffleId}/participants")
    public ResponseEntity<ParticipateRaffleResponse> getRaffleParticipate(@PathVariable Long raffleId, @LoginMemberId Long memberId) {

        ParticipateRaffleResponse raffleResponse= raffleService.participateRaffle(raffleId, memberId);

        return ResponseEntity.ok(raffleResponse);
    }
}
