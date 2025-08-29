package org.phoenix.planet.service.eco_stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock_info.response.MemberStockInfoWithDetail;
import org.phoenix.planet.mapper.MemberStockInfoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberStockInfoServiceImpl implements MemberStockInfoService {

    private final MemberStockInfoMapper memberStockInfoMapper;

    @Override
    public MemberStockInfo findPersonalStockInfoById(Long memberId, Long ecoStockId) {
        return memberStockInfoMapper.findPersonalStockInfoById(memberId, ecoStockId);
    }

    @Override
    public List<MemberStockInfoWithDetail> findAllPersonalStockInfoByMemberId(Long memberId) {
        return memberStockInfoMapper.findAllPersonalStockInfoByMemberId(memberId);
    }
}
