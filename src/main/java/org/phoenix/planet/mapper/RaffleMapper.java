package org.phoenix.planet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.raffle.RaffleDetailResponse;
import org.phoenix.planet.dto.raffle.RaffleResponse;
import org.phoenix.planet.dto.raffle.response.ParticipateRaffleResponse;

import java.util.List;

@Mapper
public interface RaffleMapper {

    List<RaffleResponse> findAll();

    List<RaffleDetailResponse> findDetailById(Long raffleId);

    void callParticipateRaffleProcedure(ParticipateRaffleResponse response);
}
