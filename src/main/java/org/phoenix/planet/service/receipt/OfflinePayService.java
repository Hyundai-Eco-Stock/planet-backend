package org.phoenix.planet.service.receipt;

import org.phoenix.planet.dto.offline_pay.request.OfflinePayload;

public interface OfflinePayService {

    void save(OfflinePayload offlinePayload);
}
