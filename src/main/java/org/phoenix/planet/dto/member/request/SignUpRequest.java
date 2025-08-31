package org.phoenix.planet.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.phoenix.planet.constant.Sex;

public record SignUpRequest(
//    @Email @NotBlank String email,
//    @NotBlank String name,
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{10,}$",
        message = "비밀번호는 영문 대소문자와 특수문자를 포함하여 10자 이상이어야 합니다."
    )
    String password,
    @NotNull Sex sex,
    @NotBlank
    @Pattern(
        regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$",
        message = "생년월일은 YYYY-MM-DD 형식이어야 합니다."
    )
    String birth,
    @NotBlank String address,
    @NotBlank String detailAddress
) {

}