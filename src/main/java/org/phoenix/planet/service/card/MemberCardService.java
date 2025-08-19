package org.phoenix.planet.service.card;

public interface MemberCardService {

    Long searchByCardCompanyIdAndCardNumber(Long cardCompanyId, String cardNumber);
}
