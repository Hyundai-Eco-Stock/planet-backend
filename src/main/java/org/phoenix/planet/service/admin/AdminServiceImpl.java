package org.phoenix.planet.service.admin;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.admin.donation.DonationAmountsByDayItem;
import org.phoenix.planet.dto.admin.donation.DonationAmountsByDayResponse;
import org.phoenix.planet.dto.admin.donation.DonatorPercentageItem;
import org.phoenix.planet.dto.admin.donation.DonatorPercentageResponse;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockHoldingAmountGroupByMemberResponse;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockIssuePercentageResponse;
import org.phoenix.planet.dto.admin.eco_stock.HoldingItem;
import org.phoenix.planet.dto.admin.eco_stock.IssueItem;
import org.phoenix.planet.dto.admin.order_product.CategoryItem;
import org.phoenix.planet.dto.admin.order_product.DayItem;
import org.phoenix.planet.dto.admin.order_product.ProductOrderDataGroupByCategoryResponse;
import org.phoenix.planet.dto.admin.order_product.ProductOrderDataGroupByDayResponse;
import org.phoenix.planet.dto.admin.phti.IssueAndOrderPatternsByPhtiItem;
import org.phoenix.planet.dto.admin.phti.IssueAndOrderPatternsByPhtiResponse;
import org.phoenix.planet.dto.admin.phti.MemberPercentageByPhtiItem;
import org.phoenix.planet.dto.admin.phti.MemberPercentageByPhtiResponse;
import org.phoenix.planet.dto.admin.response.CategorySalesResponse;
import org.phoenix.planet.dto.admin.response.OrderCountResponse;
import org.phoenix.planet.mapper.AdminMapper;
import org.phoenix.planet.mapper.MemberPhtiMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final MemberPhtiMapper memberPhtiMapper;

    @Override
    public EcoStockIssuePercentageResponse fetchEcoStockIssuePercentageData() {

        List<IssueItem> items = adminMapper.selectEcoStockIssuePercentageData();

        long totalIssued = items.stream()
                .mapToLong(IssueItem::count)
                .sum();
        int ecoStockTypes = items.size();

        return EcoStockIssuePercentageResponse.builder()
                .totalIssued(totalIssued)
                .ecoStockTypes(ecoStockTypes)
                .items(items)
                .build();
    }

    @Override
    public EcoStockHoldingAmountGroupByMemberResponse fetchEcoStockHoldingAmountDataGroupByMember() {

        List<HoldingItem> items = adminMapper.selectEcoStockHoldingAmountDataGroupByMember();

        long totalUsers = items.stream()
                .mapToLong(HoldingItem::userCount)
                .sum();
        long totalIssued = 0;
        double avgHolding = totalUsers > 0 ? (double) totalIssued / totalUsers : 0;

        return EcoStockHoldingAmountGroupByMemberResponse.builder()
                .totalUsers(totalUsers)
                .totalIssued(totalIssued)
                .avgHolding(avgHolding)
                .items(items)
                .build();
    }

    @Override
    public ProductOrderDataGroupByDayResponse fetchProductOrderDataGroupByDay() {

        List<DayItem> items = adminMapper.selectProductOrderDataGroupByDay();

        long totalOrders = items.stream()
                .mapToLong(DayItem::orders)
                .sum();
        long totalRevenue = items.stream()
                .mapToLong(DayItem::revenue)
                .sum();
        long avgOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        return ProductOrderDataGroupByDayResponse.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .avgOrderValue(avgOrderValue)
                .items(items)
                .build();
    }

    @Override
    public ProductOrderDataGroupByCategoryResponse fetchProductOrderDataGroupByCategory() {

        List<CategoryItem> items = adminMapper.selectProductOrderDataGroupByCategory();

        String topCategory = items.isEmpty() ? "" : items.getFirst().category();

        return ProductOrderDataGroupByCategoryResponse.builder()
                .topCategory(topCategory)
                .items(items)
                .build();
    }

    @Override
    public MemberPercentageByPhtiResponse fetchMemberPercentageByPhti() {

        List<MemberPercentageByPhtiItem> items = adminMapper.selectMemberPercentageByPhti();

        long totalUsers = items.stream()
                .mapToLong(MemberPercentageByPhtiItem::users)
                .sum();

        String topPhti = items.isEmpty() ? "" : items.get(0).type();

        return MemberPercentageByPhtiResponse.builder()
                .totalUsers(totalUsers)
                .topPhti(topPhti)
                .items(items)
                .build();
    }

    @Override
    public IssueAndOrderPatternsByPhtiResponse fetchIssueAndOrderPatternsByPhti() {

        List<IssueAndOrderPatternsByPhtiItem> items = adminMapper.selectIssueAndOrderPatternsByPhti();

        long totalOrders = items.stream()
                .mapToLong(IssueAndOrderPatternsByPhtiItem::orders)
                .sum();

        long avgOrders = items.isEmpty() ? 0 : totalOrders / items.size();

        return IssueAndOrderPatternsByPhtiResponse.builder()
                .avgOrders(avgOrders)
                .items(items)
                .build();
    }

    @Override
    public DonationAmountsByDayResponse fetchDonationAmountsByDay() {

        List<DonationAmountsByDayItem> items = adminMapper.selectDonationAmountsByDay();

        long totalDonation = items.stream()
                .mapToLong(DonationAmountsByDayItem::donation)
                .sum();

        return DonationAmountsByDayResponse.builder()
                .totalDonation(totalDonation)
                .items(items)
                .build();
    }

    @Override
    public DonatorPercentageResponse fetchDonatorPercentage() {

        List<DonatorPercentageItem> items = adminMapper.selectDonatorPercentage();

        long totalUsers = items.stream()
                .mapToLong(DonatorPercentageItem::users)
                .sum();

        long participation = items.stream()
                .filter(i -> i.name().equals("참여"))
                .mapToLong(DonatorPercentageItem::users)
                .sum();

        long participationRate =
                totalUsers > 0 ? Math.round((participation * 100.0) / totalUsers) : 0;

        return DonatorPercentageResponse.builder()
                .totalUsers(totalUsers)
                .participationRate(participationRate)
                .items(items)
                .build();
    }

    @Override
    public List<OrderCountResponse> fetch7DaysOrderCount() {
        return adminMapper.select7DaysOrderCount();
    }

    @Override
    public List<CategorySalesResponse> fetchCategorySales() {
        return adminMapper.selectCategorySales();
    }
}
