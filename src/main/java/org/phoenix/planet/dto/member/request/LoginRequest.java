// src/main/java/com/example/auth/dto/SignUpRequest.java

package org.phoenix.planet.dto.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Email @NotBlank String email,
    @NotBlank String password
) {

}