package org.phoenix.planet.service.offline;

import java.util.List;

public interface OfflineProductService {

    List<Long> searchTumblerProductIdList();

    List<Long> searchPaperBagProductIdList();
}
