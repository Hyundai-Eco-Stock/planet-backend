package org.phoenix.planet.dto.admin.order_product;

public record DayItem(
    String date,
    long orders,
    long revenue
) {

}