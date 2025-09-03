package org.phoenix.planet.configuration.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.dto.auth.PrincipalDetails;
import org.phoenix.planet.service.auth.AuthService;
import org.phoenix.planet.util.cookie.CookieUtil;
import org.phoenix.planet.util.file.CloudFrontFileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${frontend.origin}")
    private String frontendOrigin;

    private final AuthService authService;

    private final CookieProperties cookieProps;

    private final CloudFrontFileUtil cloudFrontFileUtil;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {

        // accessToken, refreshToken 발급
        String accessToken = authService.generateAccessToken(authentication);
        String refreshToken = authService.generateRefreshToken(authentication);

        log.info("발급된 Access Token: {}", accessToken);

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        String encodedEmail = URLEncoder.encode(principalDetails.member().getEmail(),
            StandardCharsets.UTF_8);
        String encodedName = URLEncoder.encode(principalDetails.member().getName(),
            StandardCharsets.UTF_8);

        String profileUrl = principalDetails.member().getProfileUrl();
        if (profileUrl != null) {
            if (!profileUrl.startsWith("http")) {
                profileUrl = cloudFrontFileUtil.generateUrl(profileUrl);
            }
            profileUrl = URLEncoder.encode(profileUrl, StandardCharsets.UTF_8);
        }

        String redirectUrl;
//        log.info("pwdHash: {}", principalDetails.member().getPwdHash());
        if (principalDetails.member().getPwdHash() == null) {
            redirectUrl = UriComponentsBuilder
                .fromUriString(frontendOrigin)
                .path("/signup/oauth")
                .queryParam("accessToken", accessToken)
                .queryParam("email", encodedEmail)
                .queryParam("name", encodedName)
                .queryParam("profileUrl", profileUrl)
                .build(true)
                .toUriString();
        } else {
            redirectUrl = UriComponentsBuilder
                .fromUriString(frontendOrigin)
                .path("/login/success")
                .queryParam("accessToken", accessToken)
                .queryParam("email", encodedEmail)
                .queryParam("name", encodedName)
                .queryParam("profileUrl", profileUrl)
                .build(true)
                .toUriString();
        }
        CookieUtil.addRefreshTokenCookie(response, refreshToken, cookieProps);
        response.sendRedirect(redirectUrl);


    }
}