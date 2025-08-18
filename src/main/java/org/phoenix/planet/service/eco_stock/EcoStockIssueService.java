package org.phoenix.planet.service.eco_stock;

public interface EcoStockIssueService {

    void publish(long memberId, long ecoStockId, int amount);
}
