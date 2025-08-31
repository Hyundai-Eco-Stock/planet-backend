package org.phoenix.planet.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING("결제 대기"),
    PAID("결제 완료"),
    SHIPPED("배송 중"),
    COMPLETED("배송 완료"),
    ALL_CANCELLED("전체 취소"),
    PARTIAL_CANCELLED("부분 취소"),
    DONE("구매 확정")
    ;

    private final String value;

}
