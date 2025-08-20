package org.phoenix.planet.service.offline;

import java.util.List;
import org.phoenix.planet.dto.offline.raw.OfflinePayProduct;
import org.phoenix.planet.dto.offline.raw.OfflinePayProductSaveRequest;

public interface OfflinePayProductService {

    void save(OfflinePayProductSaveRequest build);

    List<OfflinePayProduct> searchByPayHistoryId(long offlinePayHistoryId);
}
