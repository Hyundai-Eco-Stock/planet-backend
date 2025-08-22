package org.phoenix.planet.dto.car_access.raw;

import lombok.Builder;

@Builder
public record EcoCarEnterEvent(
    String carNumber
) {

}
