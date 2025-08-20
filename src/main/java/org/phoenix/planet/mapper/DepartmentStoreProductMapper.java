package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.order.raw.PickupStoreInfo;

import java.util.List;

@Mapper
public interface DepartmentStoreProductMapper {

    boolean existsByProductIdAndDepartmentStoreId(Long productId, Long departmentStoreId);

    List<PickupStoreInfo> findCommonPickupStoresByProductIds(Long productId);

}
