package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface DepartmentStoreProductMapper {

    boolean existsByProductIdAndDepartmentStoreId(Long productId, Long departmentStoreId);

    Optional<Long> findDepartmentStoreIdByProductId(Long productId);

}
