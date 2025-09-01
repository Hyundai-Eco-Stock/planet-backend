package org.phoenix.planet.service.eco_stock;

import java.time.LocalDateTime;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.dto.eco_stock.raw.EcoStockUpdatePriceRecord;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;

import java.util.List;

public interface EcoStockService {

    EcoStock searchById(long ecoStockId);

    List<EcoStock> findAll();

    List<EcoStockUpdatePriceRecord> findAllHistory(LocalDateTime time);

    void updateQuantityById(Long stockId, int updateQuantity);

    void sellStock(Long memberId, SellStockRequest sellStockRequest);
}
