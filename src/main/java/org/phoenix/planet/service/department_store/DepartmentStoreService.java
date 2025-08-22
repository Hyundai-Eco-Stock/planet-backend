package org.phoenix.planet.service.department_store;

import java.util.List;
import org.phoenix.planet.dto.department_store.response.DepartmentStoreListResponse;

public interface DepartmentStoreService {

    List<DepartmentStoreListResponse> searchAllDepartmentStoreList();
}
