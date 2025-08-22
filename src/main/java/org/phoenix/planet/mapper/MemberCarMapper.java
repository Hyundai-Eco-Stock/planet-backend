package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.constant.CarEcoType;
import org.phoenix.planet.dto.car.response.MemberCarResponse;

@Mapper
public interface MemberCarMapper {

    MemberCarResponse selectByMemberId(
        @Param("memberId") long memberId);

    void insert(
        @Param("memberId") long memberId,
        @Param("carNumber") String carNumber,
        @Param("carEcoType") CarEcoType carEcoType);
}
