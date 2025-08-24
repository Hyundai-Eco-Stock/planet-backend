package org.phoenix.planet.dto.member.request;

import jakarta.validation.constraints.NotBlank;

public record PwResetTokenRequest(
    @NotBlank String token
) {

}
