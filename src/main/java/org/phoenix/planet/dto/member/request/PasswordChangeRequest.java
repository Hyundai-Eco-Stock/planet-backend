package org.phoenix.planet.dto.member.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(
    @NotBlank String token,
    @NotBlank String password
) {

}
