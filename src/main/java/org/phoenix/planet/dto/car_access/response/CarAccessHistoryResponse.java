package org.phoenix.planet.dto.car_access.response;

public record CarAccessHistoryResponse(
    long carAccessHistoryId,
    String carNumber,
    String status,
    String createdAt // YYYY-MM-DD HH:MI:SS
) {

}
