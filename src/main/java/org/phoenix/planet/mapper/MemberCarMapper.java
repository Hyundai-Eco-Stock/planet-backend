package org.phoenix.planet.mapper;

import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.car.response.MemberCarResponse;

@Mapper
public interface MemberCarMapper {

    Optional<MemberCarResponse> selectByMemberId(
        @Param("memberId") long memberId);

    Optional<MemberCarResponse> selectByCarNumber(
        @Param("carNumber") String carNumber);

    void insert(
        @Param("memberId") long memberId,
        @Param("carNumber") String carNumber);


    void deleteByMemberId(
        @Param("memberId") long memberId);
}
