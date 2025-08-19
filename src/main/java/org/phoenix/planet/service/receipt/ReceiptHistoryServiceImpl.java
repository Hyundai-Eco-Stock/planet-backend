package org.phoenix.planet.service.receipt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.KafkaTopic;
import org.phoenix.planet.dto.offline_pay.request.OfflinePayload;
import org.phoenix.planet.producer.ReceiptEventProducer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptHistoryServiceImpl implements ReceiptHistoryService {

    private final ReceiptEventProducer receiptEventProducer;
//    private final ReceiptHistoryMapper receiptHistoryMapper;

    @Override
    public void createNoUseReceipt(PaperBagReceiptCreateRequest request) {

//        receiptHistoryMapper.createNoUseReceipt(request);
        receiptEventProducer.publish(KafkaTopic.OFFLINE_PAY_DETECTED.getValue(), null,
    }
}
