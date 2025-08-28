package org.phoenix.planet.service.offline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.mapper.OfflineShopMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflineShopServiceImpl implements OfflineShopService {

    private final OfflineShopMapper offlineShopMapper;

    @Override
    public String searchTypeById(Long shopId) {

        return offlineShopMapper.selectTypeById(shopId);
    }
}
