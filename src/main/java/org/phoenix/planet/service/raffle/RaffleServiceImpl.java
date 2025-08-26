package org.phoenix.planet.service.raffle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.raffle.RaffleResponse;
import org.phoenix.planet.mapper.RaffleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RaffleServiceImpl implements RaffleService {

    private final RaffleMapper raffleMapper;

    @Override
    public List<RaffleResponse> findAll() {
        return raffleMapper.findAll();
    }
}
