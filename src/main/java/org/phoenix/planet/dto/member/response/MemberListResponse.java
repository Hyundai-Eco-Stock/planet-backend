package org.phoenix.planet.dto.member.response;

import org.phoenix.planet.constant.member.Sex;


public record MemberListResponse(
    long id,
    String email,
    String name,
    Sex sex
) {

}
