package org.phoenix.planet.service.receipt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.receipt.PaperBagReceiptCreateRequest;
import org.phoenix.planet.producer.ReceiptEventProducer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptHistoryServiceImpl implements ReceiptHistoryService {

    private final ReceiptEventProducer receiptEventProducer;

    @Override
    public void createNoUseReceipt(PaperBagReceiptCreateRequest request) {

        receiptEventProducer.publish("eco.paper-bag-no-use-receipt-detected", null, request);
    }
}
