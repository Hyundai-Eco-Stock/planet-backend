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
import org.springframework.util.CollectionUtils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RaffleServiceImpl implements RaffleService {

    private final RaffleMapper raffleMapper;
    private final RaffleHistoryService raffleHistoryService;
    private final MemberDeviceTokenService memberDeviceTokenService;
    private final FcmService fcmService;
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

    @Override
    @Transactional
    public void raffleWinningProcess(LocalDate yesterday) {

        //래플 당철 처리 할 데이터 조회 및 지원자 조회
        List<RaffleHistoryWithDetail> raffleHistories = raffleHistoryService.findEndedYesterday(yesterday);

        log.info("지원자들 {}", raffleHistories);

        // 랜덤 당첨자 추출
        List<WinnerInfo> winnerInfos = drawRaffleWinners(raffleHistories);

        log.info("당첨자들 {}", winnerInfos);

        if (winnerInfos == null || winnerInfos.isEmpty()) {
            return; // bulkUpdate 호출 안 함
        }

        // 당첨 처리 벌크 업데이트
        raffleHistoryService.bulkUpdateWinners(winnerInfos);

        List<Long> raffleIds = raffleHistories.stream()
                .map(RaffleHistoryWithDetail::getRaffleId)
                .toList();

        raffleMapper.bulkUpdateRaffleUpdatedAt(raffleIds);
        // 당첨된 사람 FCM 토큰 조회
        Map<Long, List<String>> tokenMap =
                memberDeviceTokenService.findFcmTokensByMemberIds(winnerInfos);

        for (WinnerInfo winner : winnerInfos) {

            List<String> tokens = tokenMap.get(winner.getMemberId());

            if (tokens == null || tokens.isEmpty())
                continue;

            String body = String.format("축하합니다! %s 래플에 당첨되셨습니다.", winner.getRaffleName());

            //당첨자 알림 전송
            fcmService.sendRaffleWinNotification(tokens, body);
        }
    }


    public List<WinnerInfo> drawRaffleWinners(List<RaffleHistoryWithDetail> raffleHistories) {

        return raffleHistories.stream()
                .map(r ->
                        CollectionUtils.isEmpty(r.getRaffleHistories())
                                ? noWinner(r)                // 지원자 없으면
                                : pickRandomWinner(r))       // 지원자 있으면
                .toList();
    }

    private WinnerInfo noWinner(RaffleHistoryWithDetail raffle) {
        return WinnerInfo.builder()
                .raffleHistoryId(null)
                .memberId(null)
                .raffleName(raffle.getRaffleName())
                .build();
    }

    private WinnerInfo pickRandomWinner(RaffleHistoryWithDetail raffle) {

        List<RaffleHistory> applicants = raffle.getRaffleHistories();

        RaffleHistory winner =
                applicants.get(secureRandom.nextInt(applicants.size())); // SecureRandom 권장

        return WinnerInfo.builder()
                .raffleHistoryId(winner.getRaffleHistoryId())
                .memberId(winner.getMemberId())
                .raffleName(raffle.getRaffleName())
                .build();
    }
}
