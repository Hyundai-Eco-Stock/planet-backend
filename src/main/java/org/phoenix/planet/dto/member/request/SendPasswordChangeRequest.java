package org.phoenix.planet.dto.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendPasswordChangeRequest(
    @Email @NotBlank String email
) {

}