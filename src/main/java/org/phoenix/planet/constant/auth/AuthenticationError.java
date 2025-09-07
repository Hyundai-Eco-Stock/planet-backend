package org.phoenix.planet.constant.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthenticationError {
    INVALID_JWT_SIGNATURE("잘못된 JWT 시그니처입니다", HttpStatus.UNAUTHORIZED),
    ILLEGAL_REGISTRATION_ID("잘못된 registration id입니다(google, kakao 중 입력 가능)",
        HttpStatus.UNAUTHORIZED),

    TOKEN_EXPIRED("TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_VALID("TOKEN_NOT_VALID", HttpStatus.UNAUTHORIZED),

    ACCESS_TOKEN_EXPIRED("ACCESS_TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_NOT_VALID("ACCESS_TOKEN_NOT_VALID", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_VALID("REFRESH_TOKEN_NOT_VALID", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_REGENERATE_SUCCESS("REFRESH_TOKEN_REGENERATE_SUCCESS", HttpStatus.UNAUTHORIZED),
    DUPLICATED_EMAIL("중복된 이메일입니다", HttpStatus.UNAUTHORIZED),
    NOT_EXIST_MEMBER_ID("존재하지 않는 멤버 id 입니다", HttpStatus.UNAUTHORIZED),
    ;


    private final String value;
    private final HttpStatus httpStatus;

}
