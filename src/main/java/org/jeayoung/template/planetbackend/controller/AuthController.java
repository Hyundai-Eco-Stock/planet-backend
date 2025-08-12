package org.jeayoung.template.planetbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jeayoung.template.planetbackend.constant.AuthenticationError;
import org.jeayoung.template.planetbackend.constant.TokenKey;
import org.jeayoung.template.planetbackend.provider.TokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenProvider tokenProvider;

    @PostMapping("/access-token/regenerate")
    public ResponseEntity<Void> regenerateAccessToken(HttpServletRequest request,
        HttpServletResponse response) {

        String refreshToken = resolveRefreshTokenFromCookie(request);

        String newAccessToken = tokenProvider.regenerateAccessToken(refreshToken);
        response.setHeader(TokenKey.X_ERROR_CODE.getValue(),
            AuthenticationError.REFRESH_TOKEN_REGENERATE_SUCCESS.name());
        response.setHeader(TokenKey.AUTHORIZATION_HEADER.getValue(),
            TokenKey.AUTHENTICATION_PREFIX.getValue() + newAccessToken);
        return ResponseEntity.ok().build();
    }

    private String resolveRefreshTokenFromCookie(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }
        for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
            if ("REFRESH_TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}