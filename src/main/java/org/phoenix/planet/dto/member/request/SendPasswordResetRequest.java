// src/main/java/com/example/auth/dto/SignUpRequest.java

package org.phoenix.planet.dto.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendPasswordResetRequest(
    @Email @NotBlank String email
) {

}