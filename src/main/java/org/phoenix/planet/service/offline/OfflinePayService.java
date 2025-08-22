package org.phoenix.planet.service.offline;

import org.phoenix.planet.dto.eco_stock_certificate.request.PaperBagNoUseCertificateRequest;
import org.phoenix.planet.dto.eco_stock_certificate.request.TumblerCertificateRequest;

public interface OfflinePayService {

    void certificateTumbler(long loginMemberId,
        TumblerCertificateRequest tumblerCertificateRequest);

    void certificatePaperBagNoUse(long loginMemberId,
        PaperBagNoUseCertificateRequest paperBagNoUseCertificateRequest);
}
