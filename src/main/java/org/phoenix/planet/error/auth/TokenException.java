package org.phoenix.planet.error.auth;


import lombok.Getter;
import org.phoenix.planet.constant.AuthenticationError;
import org.springframework.security.core.AuthenticationException;

@Getter
public class TokenException extends AuthenticationException {

    private final AuthenticationError error;

    public TokenException(AuthenticationError error) {

        super(error.getValue());
        this.error = error;
    }
}