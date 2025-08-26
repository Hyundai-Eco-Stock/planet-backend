package org.phoenix.planet.dto.car.response;

public record MemberCarResponse(
    long memberCarId,
    long memberId,
    String carNumber,
    String createdAt, // YYYY-MM-DD 형식
    String updatedAt  // YYYY-MM-DD 형식
) {

}
