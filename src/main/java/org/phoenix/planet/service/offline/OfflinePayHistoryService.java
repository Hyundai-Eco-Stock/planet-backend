package org.phoenix.planet.service.offline;

import org.phoenix.planet.dto.offline.raw.OfflinePaySaveRequest;

public interface OfflinePayHistoryService {

    long save(OfflinePaySaveRequest build);
}
