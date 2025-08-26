package org.phoenix.planet.event;

import lombok.Builder;

@Builder
public record PayEvent(
    String eventName,
    long cardCompanyId,
    String cardNumber
) {

}