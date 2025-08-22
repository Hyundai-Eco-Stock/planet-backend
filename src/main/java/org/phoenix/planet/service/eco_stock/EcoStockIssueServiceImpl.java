package org.phoenix.planet.service.eco_stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.mapper.EcoStockIssueMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EcoStockIssueServiceImpl implements EcoStockIssueService {

    private final EcoStockIssueMapper ecoStockIssueMapper;

    @Override
    @Transactional
    public void issueStock(long memberId, long ecoStockId, int amount) {

        log.info("에코스톡 발급 완료");
        for (int i = 0; i < amount; i++) {
            ecoStockIssueMapper.insert(memberId, ecoStockId);
        }
    }
}
