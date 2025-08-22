package org.phoenix.planet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.dto.car_access.request.CarEnterRequest;
import org.phoenix.planet.dto.car_access.request.CarExitRequest;
import org.phoenix.planet.dto.car_access.response.CarAccessHistoryResponse;
import org.phoenix.planet.service.car_access.CarAccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars/access")
@RequiredArgsConstructor
public class CarAccessController {

    private final CarAccessService carAccessService;

    @GetMapping
    public ResponseEntity<List<CarAccessHistoryResponse>> carHistory(
    ) {
        // 출차 처리
        List<CarAccessHistoryResponse> histories = carAccessService.searchCarAccessHistories();
        return ResponseEntity.ok(histories);
    }

    @PostMapping("/enter")
    public ResponseEntity<Void> carEnter(
        @RequestBody CarEnterRequest carEnterRequest
    ) {
        // 입차 처리
        carAccessService.processEnterCar(carEnterRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/exit")
    public ResponseEntity<Void> carExit(
        @RequestBody CarExitRequest carExitRequest
    ) {
        // 출차 처리
        carAccessService.processExitCar(carExitRequest);
        return ResponseEntity.ok().build();
    }
}