package org.phoenix.planet.service.admin;

import java.util.List;
import org.phoenix.planet.dto.admin.donation.DonationAmountsByDayResponse;
import org.phoenix.planet.dto.admin.donation.DonatorPercentageResponse;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockHoldingAmountGroupByMemberResponse;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockIssuePercentageResponse;
import org.phoenix.planet.dto.admin.order_product.ProductOrderDataGroupByCategoryResponse;
import org.phoenix.planet.dto.admin.order_product.ProductOrderDataGroupByDayResponse;
import org.phoenix.planet.dto.admin.phti.IssueAndOrderPatternsByPhtiResponse;
import org.phoenix.planet.dto.admin.phti.MemberPercentageByPhtiResponse;
import org.phoenix.planet.dto.admin.raffle.RaffleParticipationByDayResponse;
import org.phoenix.planet.dto.admin.raffle.RaffleParticipationResponse;
import org.phoenix.planet.dto.admin.response.CategorySalesResponse;
import org.phoenix.planet.dto.admin.response.OrderCountResponse;

public interface AdminService {

    EcoStockIssuePercentageResponse fetchEcoStockIssuePercentageData();

    EcoStockHoldingAmountGroupByMemberResponse fetchEcoStockHoldingAmountDataGroupByMember();

    ProductOrderDataGroupByDayResponse fetchProductOrderDataGroupByDay();

    ProductOrderDataGroupByCategoryResponse fetchProductOrderDataGroupByCategory();

    MemberPercentageByPhtiResponse fetchMemberPercentageByPhti();

    IssueAndOrderPatternsByPhtiResponse fetchIssueAndOrderPatternsByPhti();

    DonationAmountsByDayResponse fetchDonationAmountsByDay();

    DonatorPercentageResponse fetchDonatorPercentage();

    RaffleParticipationResponse fetchRaffleParticipationByRaffle();

    RaffleParticipationByDayResponse fetchRaffleParticipationByDay();

    List<OrderCountResponse> fetch7DaysOrderCount();

    List<CategorySalesResponse> fetchCategorySales();
}
