package org.phoenix.planet.service.card;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.member_card.MemberCardInfoItem;
import org.phoenix.planet.dto.member_card.MemberCardRegisterRequest;
import org.phoenix.planet.dto.member_card.MemberCardsInfoResponse;
import org.phoenix.planet.mapper.MemberCardMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberCardServiceImpl implements MemberCardService {

    private final MemberCardMapper memberCardMapper;

    @Override
    public Long searchByCardCompanyIdAndCardNumber(String cardNumber) {

        return memberCardMapper.selectMemberIdByCardCompanyIdAndCardNumber(cardNumber);
    }

    @Override
    public MemberCardsInfoResponse getInfoByMemberId(long memberId) {

        List<MemberCardInfoItem> items = memberCardMapper.findByMemberId(memberId);

        return MemberCardsInfoResponse.builder()
            .memberCardInfoList(items)
            .build();
    }

    @Override
    public void registerCardInfo(long memberId, MemberCardRegisterRequest request) {

        memberCardMapper.insertCardInfo(
            memberId,
            request.cardNumber());
    }

    @Override
    public void deleteCardInfo(long memberId, long memberCardId) {

        memberCardMapper.deleteByMemberIdAndMemberCardId(memberId, memberCardId);
    }
}
