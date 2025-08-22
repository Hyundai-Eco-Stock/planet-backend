package org.phoenix.planet.service.offline;

import org.phoenix.planet.dto.offline.raw.OfflinePayHistory;

public interface OfflinePayHistoryService {

    OfflinePayHistory searchByBarcode(String code);

    void updateStockIssueStatusTrue(long offlinePayHistoryId);
}
