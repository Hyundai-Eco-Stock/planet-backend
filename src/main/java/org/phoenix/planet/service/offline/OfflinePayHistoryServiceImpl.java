package org.phoenix.planet.service.offline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.offline.raw.OfflinePayHistory;
import org.phoenix.planet.dto.offline.raw.OfflinePaySaveRequest;
import org.phoenix.planet.mapper.OfflinePayHistoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflinePayHistoryServiceImpl implements OfflinePayHistoryService {

    private final OfflinePayHistoryMapper offlinePayHistoryMapper;

    @Override
    @Transactional
    public long save(OfflinePaySaveRequest offlinePaySaveRequest) {

        offlinePayHistoryMapper.insert(offlinePaySaveRequest);
        return offlinePaySaveRequest.getOfflinePayHistoryId();
    }

    @Override
    public OfflinePayHistory searchByBarcode(String barcode) {

        return offlinePayHistoryMapper.selectByBarcode(barcode)
            .orElseThrow(() -> new IllegalArgumentException("해당 바코드를 가진 오프라인 결제 기록이 없습니다"));
    }

    @Override
    public void updateStockIssueStatusTrue(long offlinePayHistoryId) {

        offlinePayHistoryMapper.updateStockIssueStatusTrue(offlinePayHistoryId);
    }
}
