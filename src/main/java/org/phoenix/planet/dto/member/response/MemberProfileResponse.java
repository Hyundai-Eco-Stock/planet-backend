package org.phoenix.planet.dto.member.response;

import lombok.Builder;
import org.phoenix.planet.constant.Sex;

@Builder
public record MemberProfileResponse(
        String email,
        String name,
        Sex sex,
        String birth,
        String profileUrl,
        String address,
        String detailAddress
) {

}
