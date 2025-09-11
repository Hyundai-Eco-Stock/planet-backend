package org.phoenix.planet.configuration.security;

import org.springframework.stereotype.Component;

@Component
public class SecurityWhitelist {

    public static final String[] PATHS = {
        "/",
        "/error",
        "/favicon.ico",
        "/swagger-ui/**", "/v3/api-docs/**",
        "/health",
        "/ws/**",
        "/auth/access-token/regenerate", "/auth/login", "/auth/logout", "/auth/success",
        "/auth/password-change-mail", "/auth/password-change-token/valid", "/auth/change-password",
        "/auth/signup/local",
        "/eco-stock/list/**", "/eco-stock/history/**",
        "/department-stores",
        "/eco-products/today", "/eco-products/*",
        "/products/**",
        "/raffles", "/raffles/*"

    };
}