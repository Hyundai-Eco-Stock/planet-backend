package org.phoenix.planet.error.auth;

import lombok.Getter;
import org.phoenix.planet.constant.AuthenticationError;

@Getter
public class AuthException extends RuntimeException {

    private final AuthenticationError error;

    public AuthException(AuthenticationError error) {

        super(error.getValue());
        this.error = error;
    }
}
