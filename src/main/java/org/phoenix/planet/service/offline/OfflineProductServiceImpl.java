package org.phoenix.planet.service.offline;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.mapper.OfflineProductMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflineProductServiceImpl implements OfflineProductService {

    private final OfflineProductMapper offlineProductMapper;

    @Override
    public List<Long> searchTumblerProductIdList() {

        return offlineProductMapper.selectTumblerProductIdList();
    }

    @Override
    public List<Long> searchPaperBagProductIdList() {

        return offlineProductMapper.selectPaperBagProductIdList();
    }

}
