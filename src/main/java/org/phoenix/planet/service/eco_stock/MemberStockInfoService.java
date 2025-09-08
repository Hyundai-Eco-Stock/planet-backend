package org.phoenix.planet.service.eco_stock;

import java.util.List;
import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock.request.SellStockRequest;
import org.phoenix.planet.dto.eco_stock_info.response.EcoStockPriceResponse;
import org.phoenix.planet.dto.eco_stock_info.response.MemberStockInfoWithDetail;

public interface MemberStockInfoService {

    MemberStockInfo findPersonalStockInfoById(Long loginMemberId, Long ecoStockId);

    List<MemberStockInfoWithDetail> findAllPersonalStockInfoByMemberId(Long loginMemberId);

    void updateOrInsert(long memberId, long ecoStockId, int quantity);

    List<EcoStockPriceResponse> getAllEcosStockPrice();

    MemberStockInfo validateUserStock(Long loginMemberId,SellStockRequest sellCount );
}
