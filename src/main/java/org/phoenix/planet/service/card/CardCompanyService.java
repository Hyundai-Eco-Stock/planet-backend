package org.phoenix.planet.service.card;

import java.util.List;
import org.phoenix.planet.dto.card.CardCompanyListResponse;

public interface CardCompanyService {

    List<CardCompanyListResponse> searchAll();
}
