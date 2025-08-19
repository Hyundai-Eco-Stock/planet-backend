package org.phoenix.planet.service.eco_stock;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.mapper.EcoStockIssueMapper;
import org.phoenix.planet.service.fcm.FcmService;
import org.phoenix.planet.service.fcm.MemberDeviceTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EcoStockIssueServiceImpl implements EcoStockIssueService {

    private final EcoStockIssueMapper ecoStockIssueMapper;

    // 서비스
    private final FcmService fcmService;
    private final EcoStockService ecoStockService;
    private final MemberDeviceTokenService memberDeviceTokenService;

    @Override
    @Transactional
    public void publish(long memberId, long ecoStockId, int amount) {

        log.info("종이백 미사용 에코스톡 발급 완료");
        for (int i = 0; i < amount; i++) {
            ecoStockIssueMapper.insert(memberId, ecoStockId);
        }
        // 멤버 토큰 가져오기
        List<String> memberTokens = memberDeviceTokenService.getTokens(memberId);
        EcoStock ecoStock = ecoStockService.searchById(ecoStockId);
        fcmService.sendNotificationToMany(memberTokens, "에코스톡 발급", ecoStock.name() + " 1주 발급 완료");

    }
}
