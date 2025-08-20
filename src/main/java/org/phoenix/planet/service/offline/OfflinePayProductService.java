package org.phoenix.planet.service.offline;

import org.phoenix.planet.dto.offline.raw.OfflinePayProductSaveRequest;

public interface OfflinePayProductService {

    void save(OfflinePayProductSaveRequest build);
}
