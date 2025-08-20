package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.offline.raw.OfflinePaySaveRequest;

@Mapper
public interface OfflinePayHistoryMapper {

    long insert(OfflinePaySaveRequest offlinePaySaveRequest);
}
