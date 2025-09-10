package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.admin.donation.DonationAmountsByDayItem;
import org.phoenix.planet.dto.admin.donation.DonatorPercentageItem;
import org.phoenix.planet.dto.admin.eco_stock.HoldingItem;
import org.phoenix.planet.dto.admin.eco_stock.IssueItem;
import org.phoenix.planet.dto.admin.order_product.CategoryItem;
import org.phoenix.planet.dto.admin.order_product.DayItem;
import org.phoenix.planet.dto.admin.phti.IssueAndOrderPatternsByPhtiItem;
import org.phoenix.planet.dto.admin.phti.MemberPercentageByPhtiItem;
import org.phoenix.planet.dto.admin.response.CategorySalesResponse;
import org.phoenix.planet.dto.admin.response.OrderCountResponse;

@Mapper
public interface AdminMapper {

    List<IssueItem> selectEcoStockIssuePercentageData();

    List<HoldingItem> selectEcoStockHoldingAmountDataGroupByMember();

    List<DayItem> selectProductOrderDataGroupByDay();

    List<CategoryItem> selectProductOrderDataGroupByCategory();

    List<MemberPercentageByPhtiItem> selectMemberPercentageByPhti();

    List<IssueAndOrderPatternsByPhtiItem> selectIssueAndOrderPatternsByPhti();

    List<DonationAmountsByDayItem> selectDonationAmountsByDay();

    List<DonatorPercentageItem> selectDonatorPercentage();

    List<OrderCountResponse> select7DaysOrderCount();

    List<CategorySalesResponse> selectCategorySales();
}
