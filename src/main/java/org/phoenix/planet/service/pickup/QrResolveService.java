package org.phoenix.planet.service.pickup;

import org.phoenix.planet.dto.pickup.raw.OrderQrInfo;

public interface QrResolveService {

    OrderQrInfo resolve(String d);

}
