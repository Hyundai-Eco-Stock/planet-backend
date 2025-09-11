package org.phoenix.planet.dto.member.response;

import lombok.Builder;
import org.phoenix.planet.constant.Role;

@Builder
public record LoginResponse(
    String accessToken,
    String email,
    String name,
    String profileUrl,
    Role role
) {

}
