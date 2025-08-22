package org.phoenix.planet.service.card;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.card.response.CardCompanyListResponse;
import org.phoenix.planet.mapper.CardCompanyMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardCompanyServiceImpl implements CardCompanyService {

    private final CardCompanyMapper cardCompanyMapper;


    @Override
    public List<CardCompanyListResponse> searchAll() {

        return cardCompanyMapper.selectAll();
    }
}
