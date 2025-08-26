package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.raffle.RaffleResponse;

import java.util.List;

@Mapper
public interface RaffleMapper {

    List<RaffleResponse> findAll();
}
