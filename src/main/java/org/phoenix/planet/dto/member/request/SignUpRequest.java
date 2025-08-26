package org.phoenix.planet.dto.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.phoenix.planet.constant.Sex;

public record SignUpRequest(
    @Email @NotBlank String email,
    @NotBlank String name,
    @NotBlank String password,
    @NotNull Sex sex,
    @NotBlank String birth, // "YYYY-MM-DD" 형식
    @NotBlank String address,
    @NotBlank String detailAddress
) {

}