package org.phoenix.planet.service.receipt;

import org.phoenix.planet.dto.receipt.PaperBagReceiptCreateRequest;

public interface ReceiptHistoryService {

    void createNoUseReceipt(PaperBagReceiptCreateRequest request);
}
