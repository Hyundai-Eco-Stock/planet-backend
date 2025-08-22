package org.phoenix.planet.service.card;

import java.util.List;
import org.phoenix.planet.dto.card.response.CardCompanyListResponse;

public interface CardCompanyService {

    List<CardCompanyListResponse> searchAll();
}
