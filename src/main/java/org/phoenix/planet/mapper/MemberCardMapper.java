package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.phoenix.planet.dto.member_card.MemberCardInfoItem;

@Mapper
public interface MemberCardMapper {

    void insertCardInfo(
        @Param("memberId") long memberId,
        @Param("cardCompanyId") long cardCompanyId,
        @Param("cardNumber") String cardNumber);

    void deleteByMemberIdAndMemberCardId(
        @Param("memberId") long memberId,
        @Param("memberCardId") long memberCardId);

    Long selectMemberIdByCardCompanyIdAndCardNumber(
        @Param("cardCompanyId") Long cardCompanyId,
        @Param("cardNumber") String cardNumber
    );

    List<MemberCardInfoItem> findByMemberId(@Param("memberId") long memberId);
}
