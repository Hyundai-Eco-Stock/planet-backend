package org.phoenix.planet.dto.car.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.phoenix.planet.constant.CarEcoType;

public record CarRegisterRequest(
    @NotBlank
    @Pattern(
        regexp = "^(\\d{2,3})([가-힣]{1})(\\d{4})$",
        message = "차량 번호는 숫자 2~3자리 + 한글 1자리 + 숫자 4자리 형식이어야 합니다. 예: 12가3456"
    )
    String carNumber,
    
    @NotNull
    CarEcoType carEcoType
) {

}
