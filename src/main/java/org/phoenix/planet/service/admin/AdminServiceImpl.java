package org.phoenix.planet.service.admin;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockHoldingAmountGroupByMemberResponse;
import org.phoenix.planet.dto.admin.eco_stock.EcoStockIssuePercentageResponse;
import org.phoenix.planet.dto.admin.eco_stock.HoldingItem;
import org.phoenix.planet.dto.admin.eco_stock.IssueItem;
import org.phoenix.planet.mapper.AdminMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;

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
}
