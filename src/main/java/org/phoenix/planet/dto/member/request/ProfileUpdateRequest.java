package org.phoenix.planet.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.phoenix.planet.constant.Sex;

public record ProfileUpdateRequest(

    @NotNull Sex sex,
    @NotBlank String birth,
    @NotBlank String address,
    @NotBlank String detailAddress,
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{10,}$",
        message = "비밀번호는 대문자, 소문자, 특수문자를 포함하고 10자 이상이어야 합니다."
    )
    @NotBlank String oldPassword
) {

}
