package org.phoenix.planet.service.eco_stock;

import java.time.LocalDateTime;
import org.phoenix.planet.dto.eco_stock.raw.EcoStock;
import org.phoenix.planet.dto.eco_stock.raw.EcoStockUpdatePriceRecord;
import org.phoenix.planet.dto.eco_stock.raw.EcoStockWithLastPrice;
import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock.raw.PointResult;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;

import java.util.List;
import org.phoenix.planet.dto.eco_stock.response.UnifiedUpdateResult;
import org.phoenix.planet.dto.eco_stock_info.response.SellResponse;

public interface EcoStockService {

    EcoStock searchById(long ecoStockId);

    List<EcoStock> findAll();

    List<EcoStockUpdatePriceRecord> findAllHistory(LocalDateTime time);

    void updateQuantityById(Long stockId, int updateQuantity);

    SellResponse sellStock(Long memberId, SellStockRequest sellStockRequest);

    PointResult processUserTransactionAndPoint(Long loginMemberId, SellStockRequest request,
        UnifiedUpdateResult result, MemberStockInfo memberStockInfo);

    List<EcoStockWithLastPrice> findAllWithLastPrice();
}
