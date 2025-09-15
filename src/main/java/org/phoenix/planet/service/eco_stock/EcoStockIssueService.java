package org.phoenix.planet.service.eco_stock;

import org.phoenix.planet.dto.order.raw.OrderConfirmResult;
import org.phoenix.planet.dto.order.response.EcoStockIssueResponse;

public interface EcoStockIssueService {

    void issueStock(long memberId, long ecoStockId, int amount);

    EcoStockIssueResponse issueEcoStock(OrderConfirmResult orderConfirmResult, Long memberId);

    void processIssue(Long memberId, Long stockId);
}
