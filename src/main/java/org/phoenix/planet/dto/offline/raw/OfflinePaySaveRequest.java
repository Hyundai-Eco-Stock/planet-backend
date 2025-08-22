package org.phoenix.planet.dto.offline.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfflinePaySaveRequest {

    private Long offlinePayHistoryId;
    private Long shopId;
    private Long cardCompanyId;
    private Integer cardNumberLast4;
    private Long totalPrice;
    private String barcode;
}
