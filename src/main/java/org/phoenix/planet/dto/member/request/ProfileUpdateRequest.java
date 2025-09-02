package org.phoenix.planet.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.phoenix.planet.constant.Sex;

public record ProfileUpdateRequest(

    @NotNull Sex sex,
    @NotBlank String birth,
    @NotBlank String address,
    @NotBlank String detailAddress,
    @NotBlank String oldPassword
) {

}
