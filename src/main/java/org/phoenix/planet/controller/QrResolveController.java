package org.phoenix.planet.controller;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.dto.pickup.raw.OrderQrInfo;
import org.phoenix.planet.service.pickup.QrResolveService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qr")
public class QrResolveController {

    private final QrResolveService qrResolveService;

    @GetMapping("/resolve")
    public ResponseEntity<OrderQrInfo> resolve(@RequestParam("d") String d) {
        OrderQrInfo response = qrResolveService.resolve(d);
        return ResponseEntity.ok(response);
    }

}
