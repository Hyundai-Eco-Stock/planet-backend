package org.phoenix.planet.controller;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.dto.receipt.PaperBagReceiptCreateRequest;
import org.phoenix.planet.service.receipt.ReceiptHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptCreateController {

    private final ReceiptHistoryService receiptHistoryService;

    @PostMapping("/paper-bag-no-use")
    public ResponseEntity<?> createReceipt(
        @RequestBody PaperBagReceiptCreateRequest paperBagReceiptCreateRequest
    ) {

        receiptHistoryService.createNoUseReceipt(paperBagReceiptCreateRequest);
        return ResponseEntity.ok().build();
    }
}
