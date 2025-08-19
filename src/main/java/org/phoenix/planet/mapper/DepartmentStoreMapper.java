package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.department_store.response.DepartmentStoreListResponse;

@Mapper
public interface DepartmentStoreMapper {

    List<DepartmentStoreListResponse> selectAll();
}
