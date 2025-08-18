package org.phoenix.planet.dto.member.response;

import lombok.Builder;

@Builder
public record LoginResponse(
    String accessToken,
    String email,
    String name,
    String profileUrl
) {

}
