package org.phoenix.planet.service.eco_stock;

import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock_info.response.MemberStockInfoWithDetail;

import java.util.List;

public interface MemberStockInfoService {
    MemberStockInfo findPersonalStockInfoById(Long loginMemberId, Long ecoStockId);

    List<MemberStockInfoWithDetail> findAllPersonalStockInfoByMemberId(Long loginMemberId);
}
