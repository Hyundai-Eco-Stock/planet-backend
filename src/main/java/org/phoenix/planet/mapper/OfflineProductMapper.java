package org.phoenix.planet.mapper;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.offline.raw.OfflineProduct;
import org.phoenix.planet.dto.offline.response.OfflineProductListResponse;

@Mapper
public interface OfflineProductMapper {

    List<OfflineProductListResponse> selectAllByOfflineShopId(long offlineShopId);

    long selectSumPriceOfIds(List<Long> productIdList);

    Optional<OfflineProduct> selectById(Long itemId);
}
