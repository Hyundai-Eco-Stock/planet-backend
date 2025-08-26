package org.phoenix.planet.service.offline;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.offline.raw.OfflinePayProduct;
import org.phoenix.planet.mapper.OfflinePayProductMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflinePayProductServiceImpl implements OfflinePayProductService {

    private final OfflinePayProductMapper offlinePayProductMapper;

    @Override
    public List<OfflinePayProduct> searchByPayHistoryId(long offlinePayHistoryId) {

        return offlinePayProductMapper.selectByPayHistoryId(offlinePayHistoryId);
    }
}
