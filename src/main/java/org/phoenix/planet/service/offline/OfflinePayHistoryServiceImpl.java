package org.phoenix.planet.service.offline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.offline.raw.OfflinePaySaveRequest;
import org.phoenix.planet.mapper.OfflinePayHistoryMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflinePayHistoryServiceImpl implements OfflinePayHistoryService {

    private final OfflinePayHistoryMapper offlinePayHistoryMapper;

    @Override
    public long save(OfflinePaySaveRequest offlinePaySaveRequest) {
        offlinePayHistoryMapper.insert(offlinePaySaveRequest);
        return offlinePaySaveRequest.getOfflinePayHistoryId();
    }
}
