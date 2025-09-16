package org.phoenix.planet.dto.member.request;

import jakarta.validation.constraints.Email;

public record EmailExistCheckRequest(
    @Email
    String email
) {

}
