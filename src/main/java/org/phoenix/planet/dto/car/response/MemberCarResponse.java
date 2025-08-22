package org.phoenix.planet.dto.car.response;

import org.phoenix.planet.constant.CarEcoType;

public record MemberCarResponse(
    long memberCarId,
    String carNumber,
    CarEcoType carEcoType,
    String createdAt, // YYYY-MM-DD 형식
    String updatedAt  // YYYY-MM-DD 형식
) {

}
