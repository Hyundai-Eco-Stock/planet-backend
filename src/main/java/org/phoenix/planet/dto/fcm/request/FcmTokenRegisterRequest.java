package org.phoenix.planet.dto.fcm.request;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRegisterRequest(
    @NotBlank String token
) {

}
