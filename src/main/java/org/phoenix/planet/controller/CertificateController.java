package org.phoenix.planet.controller;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.eco_stock_certificate.request.TumblerCertificateRequest;
import org.phoenix.planet.service.receipt.OfflinePayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/certificate")
@RequiredArgsConstructor
public class CertificateController {

    private final OfflinePayService offlinePayService;

    @PostMapping("/tumbler")
    public ResponseEntity<?> certificateTumbler(
        @LoginMemberId long loginMemberId,
        @RequestBody TumblerCertificateRequest tumblerCertificateRequest
    ) {

        offlinePayService.certificate(loginMemberId, tumblerCertificateRequest);

        return ResponseEntity.ok()
            .build();
    }
}
