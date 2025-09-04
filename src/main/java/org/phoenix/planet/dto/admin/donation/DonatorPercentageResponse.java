package org.phoenix.planet.dto.admin.donation;

import java.util.List;
import lombok.Builder;

@Builder
public record DonatorPercentageResponse(
    long totalUsers,
    long participationRate,
    List<DonatorPercentageItem> items
) {

}
