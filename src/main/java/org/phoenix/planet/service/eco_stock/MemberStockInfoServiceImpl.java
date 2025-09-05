package org.phoenix.planet.service.eco_stock;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.eco_stock.raw.MemberStockInfo;
import org.phoenix.planet.dto.eco_stock_info.response.EcoStockPriceResponse;
import org.phoenix.planet.dto.eco_stock_info.response.MemberStockInfoWithDetail;
import org.phoenix.planet.mapper.MemberStockInfoMapper;
import org.springframework.stereotype.Service;

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

    @Override
    public void updateOrInsert(long memberId, long ecoStockId, int quantity) {
        // 1. stock_price_history 테이블에서 eco_stock_id로 최신(가장 최근 stock_time)의 가격(stock_price) 가져오기
        // 2. member_stock_info 테이블의
        //  current_total_quantity 갯수에 quantity(인자) 더하기 (업데이트)
        //  current_total_amount(내가 보유한 에코스톡의 구매 당시 가격 합)에 quantity * cur_price 더하기 (업데이트)
        memberStockInfoMapper.updateOrInsert(memberId, ecoStockId, quantity);
    }

    public List<EcoStockPriceResponse> getAllEcosStockPrice() {
        return memberStockInfoMapper.findAllEcoStockPrice();
    }
}
