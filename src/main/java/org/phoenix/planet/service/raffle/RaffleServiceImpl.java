package org.phoenix.planet.service.raffle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.RaffleError;
import org.phoenix.planet.dto.raffle.RaffleDetailResponse;
import org.phoenix.planet.dto.raffle.RaffleResponse;
import org.phoenix.planet.dto.raffle.raw.RaffleHistory;
import org.phoenix.planet.dto.raffle.raw.RaffleHistoryWithDetail;
import org.phoenix.planet.dto.raffle.raw.WinnerInfo;
import org.phoenix.planet.dto.raffle.response.ParticipateRaffleResponse;
import org.phoenix.planet.error.raffle.RaffleException;
import org.phoenix.planet.mapper.RaffleMapper;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RaffleServiceImpl implements RaffleService {

    private final RaffleMapper raffleMapper;
    private static final SecureRandom secureRandom = new SecureRandom();


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


    public List<WinnerInfo> drawRaffleWinners(List<RaffleHistoryWithDetail> raffleHistories) {

        List<WinnerInfo> winners = new ArrayList<>();

        for (RaffleHistoryWithDetail raffle : raffleHistories) {

            List<RaffleHistory> applicants = raffle.getRaffleHistories();

            if (applicants == null || applicants.isEmpty()) {

                log.info("Raffle {} ({}) : 지원자가 없음", raffle.getRaffleId(), raffle.getRaffleName());

                continue;
            }

            WinnerInfo winnerInfo = pickRandomWinner(applicants, raffle);

            winners.add(winnerInfo);

            log.info("Raffle {} ({}) 당첨자: {}", raffle.getRaffleId(), raffle.getRaffleName(), winnerInfo);
        }

        return winners;
    }

    private WinnerInfo pickRandomWinner(List<RaffleHistory> applicants,
                                        RaffleHistoryWithDetail raffle) {

        RaffleHistory winner =
                applicants.get(secureRandom.nextInt(applicants.size())); // SecureRandom 권장

        return WinnerInfo.builder()
                .raffleHistoryId(winner.getRaffleHistoryId())
                .memberId(winner.getMemberId())
                .raffleName(raffle.getRaffleName())
                .build();
    }
}
