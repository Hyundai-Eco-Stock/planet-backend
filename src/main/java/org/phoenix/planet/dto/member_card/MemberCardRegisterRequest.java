package org.phoenix.planet.dto.member_card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberCardRegisterRequest(
//    @NotNull(message = "카드사는 필수 선택입니다.")
//    Long cardCompanyId,

    @NotBlank(message = "카드 번호는 필수입니다.")
    @Size(min = 14, max = 19, message = "카드 번호는 14~19자리여야 합니다.")
    @Pattern(regexp = "^[0-9]{14,19}$", message = "카드 번호는 숫자만 입력 가능합니다.")
    String cardNumber // 카드 번호 (- 미포함)
) {

}
