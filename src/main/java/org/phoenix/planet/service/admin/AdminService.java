package org.phoenix.planet.service.admin;

import org.phoenix.planet.dto.admin.donation.DonationAmountsByDayResponse;
import org.phoenix.planet.dto.admin.donation.DonatorPercentageResponse;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockHoldingAmountGroupByMemberResponse;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockIssuePercentageResponse;
import org.phoenix.planet.dto.admin.order_product.ProductOrderDataGroupByCategoryResponse;
import org.phoenix.planet.dto.admin.order_product.ProductOrderDataGroupByDayResponse;
import org.phoenix.planet.dto.admin.phti.IssueAndOrderPatternsByPhtiResponse;
import org.phoenix.planet.dto.admin.phti.MemberPercentageByPhtiResponse;

public interface AdminService {

    EcoStockIssuePercentageResponse fetchEcoStockIssuePercentageData();

    EcoStockHoldingAmountGroupByMemberResponse fetchEcoStockHoldingAmountDataGroupByMember();

    ProductOrderDataGroupByDayResponse fetchProductOrderDataGroupByDay();

    ProductOrderDataGroupByCategoryResponse fetchProductOrderDataGroupByCategory();

    MemberPercentageByPhtiResponse fetchMemberPercentageByPhti();

    IssueAndOrderPatternsByPhtiResponse fetchIssueAndOrderPatternsByPhti();

    DonationAmountsByDayResponse fetchDonationAmountsByDay();

    DonatorPercentageResponse fetchDonatorPercentage();
}
