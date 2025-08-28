package org.phoenix.planet.service.offline;

import java.util.List;
import org.phoenix.planet.dto.offline.raw.OfflinePayProduct;

public interface OfflinePayProductService {

    List<OfflinePayProduct> searchByPayHistoryId(long offlinePayHistoryId);
}
