package org.phoenix.planet.event;

import lombok.Builder;

@Builder
public record EcoCarEnterEvent(
    String carNumber
) {

}
