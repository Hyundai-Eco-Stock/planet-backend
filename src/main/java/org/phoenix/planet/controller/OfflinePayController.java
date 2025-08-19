package org.phoenix.planet.controller;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.dto.offline_pay.request.OfflinePayload;
import org.phoenix.planet.service.receipt.OfflinePayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/offline-pays")
public class OfflinePayController {

    private final OfflinePayService offlinePayService;

    @PostMapping
    public ResponseEntity<?> createOfflinePay(
        @RequestBody OfflinePayload offlinePayload
    ) {

        offlinePayService.save(offlinePayload);
        return ResponseEntity.ok().build();
    }
}
