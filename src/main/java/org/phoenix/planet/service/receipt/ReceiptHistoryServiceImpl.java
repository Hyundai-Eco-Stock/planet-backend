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
    public void save(OfflinePayload offlinePayload) {
        // OfflinePayHistory 와 offlinePayProductHistory 에 정보 저장
        receiptEventProducer.publish(KafkaTopic.OFFLINE_PAY_DETECTED.getValue(), null,
            offlinePayload);
    }
}
