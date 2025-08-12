package org.jeayoung.template.planetbackend.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeayoung.template.planetbackend.dto.PrincipalDetails;
import org.jeayoung.template.planetbackend.provider.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    @Value("${frontend.origin}")
    private String frontendOrigin;

    private final TokenProvider tokenProvider;

    private final CookieProperties cookieProps;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {

        // accessToken, refreshToken 발급
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        log.info("Generated Refresh Token: {}", refreshToken);

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        String encodedEmail = URLEncoder.encode(principalDetails.member().getEmail(),
            StandardCharsets.UTF_8);
        String encodedName = URLEncoder.encode(principalDetails.member().getName(),
            StandardCharsets.UTF_8);
        String encodedProfile = URLEncoder.encode(principalDetails.member().getProfile(),
            StandardCharsets.UTF_8);

        String redirectUrl;
        log.info("pwdHash: {}", principalDetails.member().getPwdHash());
        if (principalDetails.member().getPwdHash() == null) {
            redirectUrl = UriComponentsBuilder
                .fromUriString(frontendOrigin)
                .path("/signup")
                .queryParam("accessToken", accessToken)
                .queryParam("email", encodedEmail)
                .queryParam("name", encodedName)
                .queryParam("profile", encodedProfile)
                .build(true)
                .toUriString();

        } else {
            redirectUrl = UriComponentsBuilder
                .fromUriString(frontendOrigin)
                .path("/login/success")
                .queryParam("accessToken", accessToken)
                .queryParam("email", encodedEmail)
                .queryParam("name", encodedName)
                .queryParam("profile", encodedProfile)
                .build(true)
                .toUriString();
        }

        ResponseCookieBuilder cookieBuilder = ResponseCookie
            .from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
            .path("/")
            .maxAge(Duration.ofDays(30))
            .sameSite(cookieProps.sameSite());

        if (cookieProps.httpOnly()) {
            cookieBuilder = cookieBuilder.httpOnly(true);
        }
        if (cookieProps.secure()) {
            cookieBuilder = cookieBuilder.secure(true);
        }
        if (cookieProps.domain() != null && !cookieProps.domain().isBlank()) {
            cookieBuilder = cookieBuilder.domain(cookieProps.domain());
        }

        response.addHeader("Set-Cookie", cookieBuilder.build().toString());
        response.sendRedirect(redirectUrl);


    }
}