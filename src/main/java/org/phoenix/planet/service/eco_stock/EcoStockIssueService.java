package org.phoenix.planet.service.eco_stock;

public interface EcoStockIssueService {

    void issueStock(long memberId, long ecoStockId, int amount);
}
