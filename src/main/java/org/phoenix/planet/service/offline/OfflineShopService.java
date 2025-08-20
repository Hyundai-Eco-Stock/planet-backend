package org.phoenix.planet.service.offline;

import java.util.List;
import org.phoenix.planet.dto.offline.response.OfflineShopListResponse;

public interface OfflineShopService {

    List<OfflineShopListResponse> searchAll();

    String searchTypeById(Long shopId);
}
