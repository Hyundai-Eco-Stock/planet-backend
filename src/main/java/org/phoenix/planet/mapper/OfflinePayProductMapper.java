package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.offline.raw.OfflinePayProductSaveRequest;

@Mapper
public interface OfflinePayProductMapper {

    void insert(OfflinePayProductSaveRequest offlinePayProductSaveRequest);
}
