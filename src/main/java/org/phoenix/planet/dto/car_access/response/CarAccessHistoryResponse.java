package org.phoenix.planet.dto.car_access.response;

public record CarAccessHistoryResponse(
    String carNumber,
    String status,
    String createdAt // YYYY-MM-DD HH:MI:SS
) {

}
