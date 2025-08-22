package org.phoenix.planet.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {

    CARD("카드 결제"),
    EASY_PAY("간편결제"),
    MOBILE_PHONE("휴대폰 결제");

    private final String value;

}
