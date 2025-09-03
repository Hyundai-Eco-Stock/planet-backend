package org.phoenix.planet.dto.admin.order_product;

import java.util.List;
import lombok.Builder;

@Builder
public record ProductOrderDataGroupByDayResponse(
    long totalOrders,
    long totalRevenue,
    long avgOrderValue,
    List<DayItem> items
) {

}
