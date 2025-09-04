package org.phoenix.planet.dto.admin.phti;

import java.util.List;
import lombok.Builder;

@Builder
public record IssueAndOrderPatternsByPhtiResponse(
    long avgOrders,
    List<IssueAndOrderPatternsByPhtiItem> items
) {

}
