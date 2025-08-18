package org.phoenix.planet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/certificate")
public class CertificateController {

    @PostMapping("/tumbler")
    public ResponseEntity<?> certificateTumbler() {

        return ResponseEntity.ok().build();
    }
}
