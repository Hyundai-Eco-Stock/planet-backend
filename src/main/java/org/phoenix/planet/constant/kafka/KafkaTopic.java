package org.phoenix.planet.constant.kafka;

import lombok.AllArgsConstructor;
import org.phoenix.planet.event.EcoCarEnterEvent;
import org.phoenix.planet.event.PayEvent;

@AllArgsConstructor
public enum KafkaTopic {
    OFFLINE_PAY_DETECTED("eco.offline-pay-detected", PayEvent.class),
    ECO_CAR_ENTER_DETECTED("eco.car-enter-detected", EcoCarEnterEvent.class),
    DEAD_LETTER("eco.dead-letter", String.class);

    private final String value;

    private final Class<?> payloadType;

    // profile prefix (기본 prod = "")
    private static String prefix = "";

    // 스프링 Environment에서 active profile 읽어서 설정
    public static void init(String p) {

        prefix = p;
    }

    // 최종 토픽 이름
    public String getTopicName() {

        return prefix + value;
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getPayloadType() {

        return (Class<T>) payloadType;
    }
}