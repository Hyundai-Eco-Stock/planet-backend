package org.jeayoung.template.planetbackend.error;

import lombok.Getter;
import org.jeayoung.template.planetbackend.constant.AuthenticationError;

@Getter
public class AuthException extends RuntimeException {

    private final AuthenticationError error;

    public AuthException(AuthenticationError error) {

        super(error.getValue());
        this.error = error;
    }
}
