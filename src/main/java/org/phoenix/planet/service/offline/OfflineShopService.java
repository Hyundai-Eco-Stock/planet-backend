package org.phoenix.planet.service.offline;

import java.util.List;
import org.phoenix.planet.dto.department_store.response.OfflineShopListResponse;

public interface OfflineShopService {

    List<OfflineShopListResponse> searchAll();
}
