package org.jeayoung.template.planetbackend.error;


import lombok.Getter;
import org.jeayoung.template.planetbackend.constant.AuthenticationError;
import org.springframework.security.core.AuthenticationException;

@Getter
public class TokenException extends AuthenticationException {

    private final AuthenticationError error;

    public TokenException(AuthenticationError error) {

        super(error.getValue());
        this.error = error;
    }
}