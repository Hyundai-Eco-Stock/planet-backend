package org.phoenix.planet.dto.member_card;

public record MemberCardInfoItem(
    long memberCardId,
    String cardNumber // 카드 번호 (- 미포함)
) {

}
