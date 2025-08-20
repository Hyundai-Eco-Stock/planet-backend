package org.phoenix.planet.service.receipt;

import org.phoenix.planet.dto.eco_stock_certificate.request.TumblerCertificateRequest;
import org.phoenix.planet.dto.offline.request.OfflinePayload;

public interface OfflinePayService {

    void save(OfflinePayload offlinePayload);

    void certificate(long loginMemberId, TumblerCertificateRequest tumblerCertificateRequest);
}
