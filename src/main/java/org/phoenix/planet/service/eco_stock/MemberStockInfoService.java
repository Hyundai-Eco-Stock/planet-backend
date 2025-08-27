package org.phoenix.planet.service.eco_stock;

import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;

public interface MemberStockInfoService {
    MemberStockInfo findPersonalStockInfoById(Long loginMemberId, Long ecoStockId);
}
