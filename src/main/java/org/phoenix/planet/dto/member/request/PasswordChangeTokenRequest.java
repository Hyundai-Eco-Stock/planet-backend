package org.phoenix.planet.dto.member.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeTokenRequest(
    @NotBlank String token
) {

}
