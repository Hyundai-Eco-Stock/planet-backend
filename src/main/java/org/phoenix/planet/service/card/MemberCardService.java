package org.phoenix.planet.service.card;

import org.phoenix.planet.dto.member_card.MemberCardRegisterRequest;
import org.phoenix.planet.dto.member_card.MemberCardsInfoResponse;

public interface MemberCardService {

    Long searchByCardCompanyIdAndCardNumber(String cardNumber);

    MemberCardsInfoResponse getInfoByMemberId(long memberId);

    void registerCardInfo(long memberId, MemberCardRegisterRequest request);

    void deleteCardInfo(long memberId, long memberCardId);
}
