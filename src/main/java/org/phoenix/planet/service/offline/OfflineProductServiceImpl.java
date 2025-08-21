package org.phoenix.planet.service.offline;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.offline.raw.OfflineProduct;
import org.phoenix.planet.dto.offline.response.OfflineProductListResponse;
import org.phoenix.planet.mapper.OfflineProductMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflineProductServiceImpl implements OfflineProductService {

    private final OfflineProductMapper offlineProductMapper;

    @Override
    public List<OfflineProductListResponse> searchAllByShopId(long shopId) {

        return offlineProductMapper.selectAllByOfflineShopId(shopId);
    }

    @Override
    public long getTotalPriceByIds(List<Long> productIdList) {

        return offlineProductMapper.selectSumPriceOfIds(productIdList);
    }

    @Override
    public OfflineProduct searchById(Long productId) {

        return offlineProductMapper.selectById(productId)
            .orElseThrow(() -> new IllegalArgumentException("item id에 해당하는 오프라인 상품 정보가 없습니다"));
    }

    @Override
    public List<OfflineProduct> searchByIds(List<Long> productIds) {

        return offlineProductMapper.selectByIds(productIds);
    }

    @Override
    public List<Long> searchTumblerProductIdList() {

        return offlineProductMapper.selectTumblerProductIdList();
    }

    @Override
    public List<Long> searchPaperBagProductIdList() {

        return offlineProductMapper.selectPaperBagProductIdList();
    }

}
