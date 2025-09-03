package org.phoenix.planet.service.admin;

import org.phoenix.planet.dto.admin.eco_stock.EcoStockHoldingAmountGroupByMemberResponse;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockIssuePercentageResponse;
import org.phoenix.planet.dto.admin.order_product.ProductOrderDataGroupByCategoryResponse;
import org.phoenix.planet.dto.admin.order_product.ProductOrderDataGroupByDayResponse;

public interface AdminService {

    EcoStockIssuePercentageResponse fetchEcoStockIssuePercentageData();

    EcoStockHoldingAmountGroupByMemberResponse fetchEcoStockHoldingAmountDataGroupByMember();

    ProductOrderDataGroupByDayResponse fetchProductOrderDataGroupByDay();

    ProductOrderDataGroupByCategoryResponse fetchProductOrderDataGroupByCategory();
}
