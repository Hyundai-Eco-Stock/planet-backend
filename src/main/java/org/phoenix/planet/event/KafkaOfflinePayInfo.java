package org.phoenix.planet.event;

import java.util.List;
import lombok.Builder;
import org.phoenix.planet.dto.offline.request.OfflinePayload.Item;
import org.phoenix.planet.dto.offline.request.OfflinePayload.Summary;

@Builder
public record KafkaOfflinePayInfo(
    long offlinePayHistoryId,
    int posId,
    long dailySeq,
    long shopId,
    long cardCompanyId,
    String cardNumber,
    int last4,
    List<Item> items,
    Summary summary
) {

}