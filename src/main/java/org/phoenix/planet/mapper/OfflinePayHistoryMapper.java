package org.phoenix.planet.mapper;

import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.offline.raw.OfflinePayHistory;
import org.phoenix.planet.dto.offline.raw.OfflinePaySaveRequest;

@Mapper
public interface OfflinePayHistoryMapper {

    long insert(OfflinePaySaveRequest offlinePaySaveRequest);

    Optional<OfflinePayHistory> selectByBarcode(String barcode);

    void updateStockIssueStatusTrue(long offlinePayHistoryId);
}
