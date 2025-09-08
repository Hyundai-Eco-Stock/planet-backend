package org.phoenix.planet.service.eco_stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;
import org.phoenix.planet.dto.eco_stock.response.UnifiedUpdateResult;
import org.phoenix.planet.mapper.TransactionHistoryMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    private final TransactionHistoryMapper transactionHistoryMapper;

    @Override
    public void save(Long memberId, SellStockRequest request, UnifiedUpdateResult result,
        MemberStockInfo memberStockInfo) {
        transactionHistoryMapper.save(memberStockInfo.memberStockInfoId(),result,request.getSellCount());
    }
}
