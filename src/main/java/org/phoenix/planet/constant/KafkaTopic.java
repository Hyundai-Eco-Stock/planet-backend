package org.phoenix.planet.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.phoenix.planet.dto.car_access.raw.EcoCarEnterEvent;
import org.phoenix.planet.dto.offline.raw.KafkaOfflinePayInfo;

@Getter
@AllArgsConstructor
public enum KafkaTopic {
    OFFLINE_PAY_DETECTED("eco.offline-pay-detected", KafkaOfflinePayInfo.class),
    ECO_CAR_ENTER_DETECTED("eco.car-enter-detected", EcoCarEnterEvent.class),
    DEAD_LETTER("eco.dead-letter", String.class);

    private final String value;
    private final Class<?> payloadType;

    // 제네릭 메서드로 안전하게 캐스팅
    @SuppressWarnings("unchecked")
    public <T> Class<T> getPayloadType() {

        return (Class<T>) payloadType;
    }

    // 상수로도 노출
    public static final String OFFLINE_PAY_DETECTED_VALUE = "eco.offline-pay-detected";
    public static final String ECO_CAR_ENTER_DETECTED_VALUE = "eco.car-enter-detected";
    public static final String DEAD_LETTER_VALUE = "eco.dead-letter";
}