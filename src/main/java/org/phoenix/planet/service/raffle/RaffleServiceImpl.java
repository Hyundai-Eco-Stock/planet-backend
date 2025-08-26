package org.phoenix.planet.service.raffle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.RaffleError;
import org.phoenix.planet.dto.raffle.RaffleDetailResponse;
import org.phoenix.planet.dto.raffle.RaffleResponse;
import org.phoenix.planet.dto.raffle.response.ParticipateRaffleResponse;
import org.phoenix.planet.error.raffle.RaffleException;
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

    @Override
    public List<RaffleDetailResponse> findDetailById(Long raffleId) {
        return raffleMapper.findDetailById(raffleId);
    }

    @Override
    public void participateRaffle(Long raffleId, Long memberId) {

        try {
            log.info("래플 참여 시작 - raffleId: {}, memberId: {}", raffleId, memberId);

            ParticipateRaffleResponse response =
                    ParticipateRaffleResponse.builder()
                    .memberId(memberId)
                    .raffleId(raffleId)
                    .build();

            raffleMapper.callParticipateRaffleProcedure(response);

            log.info("프로시저 실행 결과: {}", response.getResult());

            // 결과에 따른 예외 처리
            handleProcedureResult(response);

            log.info("래플 참여 성공 - raffleId: {}, memberId: {}", raffleId, memberId);

        } catch (RaffleException e) {

            log.warn("래플 참여 실패 - raffleId: {}, memberId: {}, error: {}", raffleId, memberId, e.getMessage());

            throw e; // 이미 정의된 예외는 재전파

        } catch (Exception e) {

            log.error("래플 참여 중 시스템 오류 - raffleId: {}, memberId: {}", raffleId, memberId, e);

            throw new RaffleException(RaffleError.RAFFLE_SYSTEM_ERROR);
        }
    }

    private void handleProcedureResult(ParticipateRaffleResponse response) {

        if (response == null || response.getResult() == null) {

            log.error("프로시저 응답이 null입니다");

            throw new RaffleException(RaffleError.RAFFLE_SYSTEM_ERROR);
        }

        switch (response.getResult()) {
            case 1:  // 성공
                return;
            case -2: // 래플없음/기간만료
                throw new RaffleException(RaffleError.RAFFLE_NOT_FOUND);
            case -3: // 중복참여
                throw new RaffleException(RaffleError.DUPLICATE_PARTICIPATION);
            case -5: // 수량부족/미보유
                throw new RaffleException(RaffleError.INSUFFICIENT_STOCK);
            case 0:  // 시스템 에러
                throw new RaffleException(RaffleError.RAFFLE_SYSTEM_ERROR);
            default: // 예상치 못한 결과값
                log.error("예상치 못한 프로시저 결과값: {}", response.getResult());
                throw new RaffleException(RaffleError.RAFFLE_SYSTEM_ERROR);
        }
    }
}
