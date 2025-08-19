package org.phoenix.planet.service.offline;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.department_store.response.OfflineShopListResponse;
import org.phoenix.planet.mapper.OfflineShopMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflineShopServiceImpl implements OfflineShopService {

    private final OfflineShopMapper offlineShopMapper;

    @Override
    public List<OfflineShopListResponse> searchAll() {

        return offlineShopMapper.selectAll();
    }

    @Override
    public String searchTypeById(Long shopId) {

        return offlineShopMapper.selectTypeById(shopId);
    }
}
