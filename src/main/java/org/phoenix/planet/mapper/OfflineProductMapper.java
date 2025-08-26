package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OfflineProductMapper {

    List<Long> selectTumblerProductIdList();

    List<Long> selectPaperBagProductIdList();
}
