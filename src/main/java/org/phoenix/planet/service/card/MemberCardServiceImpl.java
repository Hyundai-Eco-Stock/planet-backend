package org.phoenix.planet.service.card;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.mapper.MemberCardMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberCardServiceImpl implements MemberCardService {

    private final MemberCardMapper memberCardMapper;

    @Override
    public Long searchByCardCompanyIdAndCardNumber(Long cardCompanyId, String cardNumber) {

        return memberCardMapper.selectMemberIdByCardCompanyIdAndCardNumber(cardCompanyId,
            cardNumber);
    }
}
