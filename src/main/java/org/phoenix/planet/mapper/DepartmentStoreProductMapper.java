package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepartmentStoreProductMapper {

    boolean existsByProductIdAndDepartmentStoreId(Long productId, Long departmentStoreId);

}
