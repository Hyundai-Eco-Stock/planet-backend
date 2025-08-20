package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.offline.response.OfflineProductListResponse;

@Mapper
public interface OfflineProductMapper {

    List<OfflineProductListResponse> selectAllByOfflineShopId(long offlineShopId);
}
