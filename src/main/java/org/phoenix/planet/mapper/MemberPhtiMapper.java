package org.phoenix.planet.mapper;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.phti.raw.MemberPhti;
import org.phoenix.planet.dto.phti.raw.MemberPhtiSaveRequest;
import org.phoenix.planet.dto.phti.raw.PhtiResultResponse;

@Mapper
public interface MemberPhtiMapper {

    void insertOrUpdate(MemberPhtiSaveRequest memberPhtiSaveRequest);

    List<MemberPhti> selectAll();

    Optional<PhtiResultResponse> selectPhtiResultByMemberId(@Param("memberId") long memberId);
}
