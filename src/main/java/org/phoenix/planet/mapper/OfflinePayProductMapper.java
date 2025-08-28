package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.offline.raw.OfflinePayProduct;
import org.phoenix.planet.dto.offline.raw.OfflinePayProductSaveRequest;

@Mapper
public interface OfflinePayProductMapper {

    void insert(OfflinePayProductSaveRequest offlinePayProductSaveRequest);

    // TODO 삭제 예정
    List<OfflinePayProduct> selectByPayHistoryId(long offlinePayHistoryId);
}
