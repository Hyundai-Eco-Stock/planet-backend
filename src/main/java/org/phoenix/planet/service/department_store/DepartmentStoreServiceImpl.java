package org.phoenix.planet.service.department_store;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.department_store.response.DepartmentStoreListResponse;
import org.phoenix.planet.mapper.DepartmentStoreMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentStoreServiceImpl implements DepartmentStoreService {


    private final DepartmentStoreMapper departmentStoreMapper;

    @Override
    public List<DepartmentStoreListResponse> searchAllDepartmentStoreList() {

        return departmentStoreMapper.selectAll();
    }
}
