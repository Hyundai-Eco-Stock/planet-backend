package org.phoenix.planet.mapper;

import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.offline.raw.OfflinePayHistory;

@Mapper
public interface OfflinePayHistoryMapper {

    Optional<OfflinePayHistory> selectByBarcode(String barcode);

    void updateStockIssueStatusTrue(long offlinePayHistoryId);
}
