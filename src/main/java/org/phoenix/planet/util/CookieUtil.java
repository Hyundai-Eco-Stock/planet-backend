package org.phoenix.planet.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.phoenix.planet.configuration.CookieProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;

public class CookieUtil {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    /**
     * Refresh 토큰 저장 쿠키 생성 & 응답에 추가
     */
    public static void addRefreshTokenCookie(
        HttpServletResponse response,
        String refreshToken,
        CookieProperties props
    ) {

        ResponseCookieBuilder builder = ResponseCookie
            .from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
            .path("/")
            .maxAge(Duration.ofDays(30)) // TODO: jwt와 통일하기
            .sameSite(props.sameSite());

        if (props.httpOnly()) {
            builder.httpOnly(true);
        }
        if (props.secure()) {
            builder.secure(true);
        }
        if (props.domain() != null && !props.domain().isBlank()) {
            builder.domain(props.domain());
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    public static String resolveRefreshTokenFromCookie(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie c : cookies) {
            if (REFRESH_TOKEN_COOKIE_NAME.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    /**
     * ✅ 환경(CookieProperties)에 맞춰 Refresh 토큰 쿠키 무효화(삭제) 헤더 추가 - 발급 시와 동일한
     * 속성(domain/path/samesite/secure/httponly)을 그대로 적용해야 삭제가 확실함
     */
    public static void invalidateRefreshToken(
        HttpServletResponse response,
        CookieProperties props
    ) {

        ResponseCookieBuilder builder = ResponseCookie
            .from(REFRESH_TOKEN_COOKIE_NAME, "")
            .path("/")
            .maxAge(0)                       // 즉시 만료
            .sameSite(props.sameSite());

        if (props.httpOnly()) {
            builder.httpOnly(true);
        }
        if (props.secure()) {
            builder.secure(true);
        }
        if (props.domain() != null && !props.domain().isBlank()) {
            builder.domain(props.domain());
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }
}
