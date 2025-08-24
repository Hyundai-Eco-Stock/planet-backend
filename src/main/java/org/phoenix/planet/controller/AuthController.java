package org.phoenix.planet.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.annotation.LoginMemberId;
import org.phoenix.planet.configuration.security.CookieProperties;
import org.phoenix.planet.constant.AuthenticationError;
import org.phoenix.planet.constant.TokenKey;
import org.phoenix.planet.dto.auth.PrincipalDetails;
import org.phoenix.planet.dto.member.request.LoginRequest;
import org.phoenix.planet.dto.member.request.PasswordResetRequest;
import org.phoenix.planet.dto.member.request.PwResetTokenRequest;
import org.phoenix.planet.dto.member.request.SendPasswordResetRequest;
import org.phoenix.planet.dto.member.request.SignUpRequest;
import org.phoenix.planet.dto.member.response.LoginResponse;
import org.phoenix.planet.service.auth.AuthService;
import org.phoenix.planet.service.member.MemberService;
import org.phoenix.planet.util.cookie.CookieUtil;
import org.phoenix.planet.util.file.CloudFrontFileUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final CloudFrontFileUtil cloudFrontFileUtil;

    private final CookieProperties cookieProps;

    @PostMapping(
        value = "/signup/kakao",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> signUpByKakao(
        @RequestPart("signUp") @Valid SignUpRequest request,
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
        @LoginMemberId long loginMemberId
    ) {

        memberService.signUp(loginMemberId, request, profileImage);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        HttpServletRequest request,
        HttpServletResponse response
    ) {

        authService.invalidateRefreshToken(request);
        CookieUtil.invalidateRefreshToken(response, cookieProps);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/access-token/regenerate")
    public ResponseEntity<Void> regenerateAccessToken(HttpServletRequest request,
        HttpServletResponse response) {

        String refreshToken = resolveRefreshTokenFromCookie(request);

        String newAccessToken = authService.regenerateAccessToken(refreshToken);
        response.setHeader(TokenKey.X_ERROR_CODE.getValue(),
            AuthenticationError.REFRESH_TOKEN_REGENERATE_SUCCESS.name());
        response.setHeader(TokenKey.AUTHORIZATION_HEADER.getValue(),
            TokenKey.AUTHENTICATION_PREFIX.getValue() + newAccessToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> localLogin(
        HttpServletResponse response,
        @RequestBody LoginRequest loginRequest
    ) {

        Authentication authentication = authService.login(loginRequest);
        String accessToken = authService.generateAccessToken(authentication);
        String refreshToken = authService.generateRefreshToken(authentication);

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        CookieUtil.addRefreshTokenCookie(response, refreshToken, cookieProps);

        String profileUrl = principalDetails.member().getProfileUrl();
        if (profileUrl != null) {
            if (!profileUrl.startsWith("http")) {
                profileUrl = cloudFrontFileUtil.generateSignedUrl(profileUrl, 60);
            }
        }

        return ResponseEntity.ok(LoginResponse.builder()
            .accessToken(accessToken)
            .email(principalDetails.member().getEmail())
            .name(principalDetails.member().getName())
            .profileUrl(profileUrl)
            .build());
    }

    @PostMapping("/password-reset-mail")
    public ResponseEntity<Void> sendPasswordResetMail(
        @RequestBody SendPasswordResetRequest request
    ) {

        authService.sendPasswordResetMail(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset-token/valid")
    public ResponseEntity<Void> validatePwResetToken(
        @RequestBody PwResetTokenRequest request
    ) {

        authService.validatePwResetToken(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
        @RequestBody PasswordResetRequest request
    ) {

        authService.ResetPassword(request);
        return ResponseEntity.ok().build();
    }

    private String resolveRefreshTokenFromCookie(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("REFRESH_TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}