package org.phoenix.planet.service.offline;

import java.util.List;
import org.phoenix.planet.dto.offline.response.OfflineProductListResponse;

public interface OfflineProductService {

    List<OfflineProductListResponse> searchAllByShopId(long shopId);
}
