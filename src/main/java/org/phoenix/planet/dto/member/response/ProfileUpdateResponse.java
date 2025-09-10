package org.phoenix.planet.dto.member.response;

import lombok.Builder;

@Builder
public record ProfileUpdateResponse(
    String profileImgUrl
) {

}
