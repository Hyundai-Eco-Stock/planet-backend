package org.phoenix.planet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.dto.eco_stock_certificate.request.PaperBagNoUseCertificateRequest;
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
        @RequestBody @Valid TumblerCertificateRequest tumblerCertificateRequest
    ) {

        offlinePayService.certificateTumbler(loginMemberId, tumblerCertificateRequest);

        return ResponseEntity.ok()
            .build();
    }

    @PostMapping("/paper-bag-no-use")
    public ResponseEntity<?> certificatePaperBagNoUse(
        @LoginMemberId long loginMemberId,
        @RequestBody @Valid PaperBagNoUseCertificateRequest paperBagNoUseCertificateRequest
    ) {

        offlinePayService.certificatePaperBagNoUse(loginMemberId, paperBagNoUseCertificateRequest);

        return ResponseEntity.ok()
            .build();
    }
}
