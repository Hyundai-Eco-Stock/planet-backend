package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.card.response.CardCompanyListResponse;

@Mapper
public interface CardCompanyMapper {

    List<CardCompanyListResponse> selectAll();
}
