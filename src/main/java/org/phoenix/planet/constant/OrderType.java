package org.phoenix.planet.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderType {

    DELIVERY("일반 배송"),
    PICKUP("매장 픽업")
    ;

    private final String value;

}
