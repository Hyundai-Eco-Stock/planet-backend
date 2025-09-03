package org.phoenix.planet.service.admin;

import org.phoenix.planet.dto.admin.eco_stock.EcoStockHoldingAmountGroupByMemberResponse;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockIssuePercentageResponse;

public interface AdminService {

    EcoStockIssuePercentageResponse fetchEcoStockIssuePercentageData();

    EcoStockHoldingAmountGroupByMemberResponse fetchEcoStockHoldingAmountDataGroupByMember();
}
