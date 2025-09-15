package org.phoenix.planet.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.phoenix.planet.dto.member.request.EmailExistCheckRequest;
import org.phoenix.planet.dto.member.request.LoginRequest;
import org.phoenix.planet.dto.member.request.PasswordChangeRequest;
import org.phoenix.planet.dto.member.request.PasswordChangeTokenRequest;
import org.phoenix.planet.dto.member.request.SendPasswordChangeRequest;
import org.phoenix.planet.dto.member.response.EmailExistCheckResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {

    /**
     * refresh 토큰을 이용하여 Access 토큰 재발급
     *
     * @param refreshToken
     * @return
     */
    String regenerateAccessToken(String refreshToken);

    /**
     * Access 토큰 발급
     *
     * @param authentication
     * @return
     */
    String generateAccessToken(Authentication authentication);

    /**
     * Refresh 토큰 발급 & Redis에 저장
     *
     * @param authentication
     * @return
     */
    String generateRefreshToken(Authentication authentication);

    /**
     * 토큰 만료기간이 아직 도달하지 않았는지 여부 확인
     *
     * @param token
     * @return
     */
    void validateToken(String token);

    void invalidateRefreshToken(HttpServletRequest request);

    Authentication getAuthentication(String token);

    Authentication login(LoginRequest loginRequest);

    void sendPasswordChangeMail(SendPasswordChangeRequest request);

    void validatePasswordChangeToken(PasswordChangeTokenRequest request);

    void changePassword(PasswordChangeRequest request);

    EmailExistCheckResponse validateEmailExist(@Valid EmailExistCheckRequest request);
}