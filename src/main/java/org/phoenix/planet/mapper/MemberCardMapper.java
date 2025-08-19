package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberCardMapper {

    Long selectMemberIdByCardCompanyIdAndCardNumber(
        @Param("cardCompanyId") Long cardCompanyId,
        @Param("cardNumber") String cardNumber
    );
}
