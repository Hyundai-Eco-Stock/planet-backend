package org.phoenix.planet.service.offline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.offline.raw.OfflinePayHistory;
import org.phoenix.planet.mapper.OfflinePayHistoryMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflinePayHistoryServiceImpl implements OfflinePayHistoryService {

    private final OfflinePayHistoryMapper offlinePayHistoryMapper;

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
