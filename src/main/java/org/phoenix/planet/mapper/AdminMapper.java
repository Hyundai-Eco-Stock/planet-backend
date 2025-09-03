package org.phoenix.planet.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.phoenix.planet.dto.admin.eco_stock.HoldingItem;
import org.phoenix.planet.dto.admin.eco_stock.IssueItem;
import org.phoenix.planet.dto.admin.order_product.CategoryItem;
import org.phoenix.planet.dto.admin.order_product.DayItem;

@Mapper
public interface AdminMapper {

    List<IssueItem> selectEcoStockIssuePercentageData();

    List<HoldingItem> selectEcoStockHoldingAmountDataGroupByMember();

    List<DayItem> selectProductOrderDataGroupByDay();

    List<CategoryItem> selectProductOrderDataGroupByCategory();
}
