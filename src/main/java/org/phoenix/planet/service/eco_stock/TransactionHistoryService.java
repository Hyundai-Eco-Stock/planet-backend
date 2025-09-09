package org.phoenix.planet.service.eco_stock;

import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;
import org.phoenix.planet.dto.eco_stock.response.UnifiedUpdateResult;

public interface TransactionHistoryService {

    void save(Long loginMemberId, SellStockRequest request, UnifiedUpdateResult result,
        MemberStockInfo memberStockInfo);
}
