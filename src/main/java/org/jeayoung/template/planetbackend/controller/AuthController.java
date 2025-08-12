package org.jeayoung.template.planetbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jeayoung.template.planetbackend.annotation.LoginMemberId;
import org.jeayoung.template.planetbackend.configuration.CookieProperties;
import org.jeayoung.template.planetbackend.constant.AuthenticationError;
import org.jeayoung.template.planetbackend.constant.TokenKey;
import org.jeayoung.template.planetbackend.dto.member.request.SignUpRequest;
import org.jeayoung.template.planetbackend.provider.TokenProvider;
import org.jeayoung.template.planetbackend.service.AuthService;
import org.jeayoung.template.planetbackend.util.CookieUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthService authService;

    // 향후엔 없앨게.. (cookie 유틸이 @component scan 해야 되지 않나)
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

        authService.signUp(loginMemberId, request, profileImage);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        HttpServletRequest request,
        HttpServletResponse response
    ) {

        // 쿠키에서 Refresh 추출 → 블랙리스트 등록
        tokenProvider.invalidateRefreshToken(request);
        // 브라우저 쿠키 즉시 만료
        CookieUtil.invalidateRefreshToken(response, cookieProps);
        return ResponseEntity.ok().build();
    }

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