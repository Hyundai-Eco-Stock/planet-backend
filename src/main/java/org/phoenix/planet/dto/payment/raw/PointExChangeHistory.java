package org.phoenix.planet.dto.payment.raw;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PointExChangeHistory {

    private Long pointExchangeHistoryId;

    private Integer pointPrice;

    private String status;

    private Long memberId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
