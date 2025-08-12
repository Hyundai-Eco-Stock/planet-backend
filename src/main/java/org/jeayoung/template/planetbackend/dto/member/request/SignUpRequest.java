// src/main/java/com/example/auth/dto/SignUpRequest.java

package org.jeayoung.template.planetbackend.dto.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
    @Email @NotBlank String email,
    @NotBlank String name,
    @NotBlank String password
) {

}