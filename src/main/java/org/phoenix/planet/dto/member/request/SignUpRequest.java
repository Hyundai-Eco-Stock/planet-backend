package org.phoenix.planet.dto.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.phoenix.planet.constant.Sex;

public record SignUpRequest(
    @Email @NotBlank String email,
    @NotBlank String name,
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{10,}$",
        message = "비밀번호는 영문 대소문자와 특수문자를 포함하여 10자 이상이어야 합니다."
    )
    String password,
    @NotNull Sex sex,
    @NotBlank String birth, // "YYYY-MM-DD" 형식
    @NotBlank String address,
    @NotBlank String detailAddress
) {

}