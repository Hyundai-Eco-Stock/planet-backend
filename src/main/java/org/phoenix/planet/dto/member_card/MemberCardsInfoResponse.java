package org.phoenix.planet.dto.member_card;

import java.util.List;
import lombok.Builder;

@Builder
public record MemberCardsInfoResponse(
    List<MemberCardInfoItem> memberCardInfoList
) {

}
