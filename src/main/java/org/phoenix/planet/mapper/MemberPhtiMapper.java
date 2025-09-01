package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.phti.raw.MemberPhti;
import org.phoenix.planet.dto.phti.raw.MemberPhtiSaveRequest;

@Mapper
public interface MemberPhtiMapper {

    void insertOrUpdate(MemberPhtiSaveRequest memberPhtiSaveRequest);

    List<MemberPhti> selectAll();
}
