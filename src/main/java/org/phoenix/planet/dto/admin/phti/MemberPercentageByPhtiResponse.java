package org.phoenix.planet.dto.admin.phti;

import java.util.List;
import lombok.Builder;

@Builder
public record MemberPercentageByPhtiResponse(
    long totalUsers,
    String topPhti,
    List<MemberPercentageByPhtiItem> items
) {

}
