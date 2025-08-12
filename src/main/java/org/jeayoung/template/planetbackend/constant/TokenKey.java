package org.jeayoung.template.planetbackend.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenKey {
    AUTHENTICATION_PREFIX("Bearer "),
    X_ERROR_CODE("X-Error-Code"),
    AUTHORIZATION_HEADER("Authorization"),
    REFRESH_TOKEN("REFRESH_TOKEN"),
    ;

    private final String value;
}
