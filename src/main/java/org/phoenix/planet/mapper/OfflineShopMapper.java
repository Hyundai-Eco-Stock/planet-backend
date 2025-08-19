package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.department_store.response.OfflineShopListResponse;

@Mapper
public interface OfflineShopMapper {

    List<OfflineShopListResponse> selectAll();

    String selectTypeById(Long shopId);
}
