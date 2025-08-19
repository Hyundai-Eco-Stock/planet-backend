package org.phoenix.planet.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KafkaTopic {
    OFFLINE_PAY_DETECTED("eco.offline-pay-detected"),
    DEAD_LETTER("eco.dead-letter"),
    ;

    private final String value;

    // ✅ 상수로도 노출
    public static final String OFFLINE_PAY_DETECTED_VALUE = "eco.offline-pay-detected";
    public static final String DEAD_LETTER_VALUE = "eco.dead-letter";
}
