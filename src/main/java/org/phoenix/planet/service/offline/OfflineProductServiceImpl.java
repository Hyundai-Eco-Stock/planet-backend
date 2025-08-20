package org.phoenix.planet.service.offline;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
