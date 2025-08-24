package org.phoenix.planet.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.AuthenticationError;
import org.phoenix.planet.constant.Role;
import org.phoenix.planet.dto.auth.PrincipalDetails;
import org.phoenix.planet.dto.member.raw.Member;
import org.phoenix.planet.dto.member.request.LoginRequest;
import org.phoenix.planet.dto.member.request.PasswordResetRequest;
import org.phoenix.planet.dto.member.request.PwResetTokenRequest;
import org.phoenix.planet.dto.member.request.SendPasswordResetRequest;
import org.phoenix.planet.error.auth.TokenException;
import org.phoenix.planet.mapper.MemberMapper;
import org.phoenix.planet.repository.PasswordResetTokenRepository;
import org.phoenix.planet.service.mail.MailService;
import org.phoenix.planet.service.member.MemberService;
import org.phoenix.planet.util.cookie.CookieUtil;
import org.phoenix.planet.util.token.TokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberService memberService;
    @Value("${jwt.key}")
    private String key;

    @Value("${jwt.access-token-expire-millis}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.refresh-token-expire-millis}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    @Value("${frontend.origin}")
    private String frontendBaseUrl;

    private SecretKey secretKey;

    private final String KEY_ROLE = "role";
    private final String KEY_MEMBER_ID = "memberId";

    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final SecureRandom secureRandom;

    private final MemberMapper memberMapper;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final MailService mailService;

    @PostConstruct
    private void setSecretKey() {

        secretKey = Keys.hmacShaKeyFor(key.getBytes());
        log.info("JWT Secret Key initialized.");
    }

    public String regenerateAccessToken(String refreshToken) {

        try {
            validateToken(refreshToken);
        } catch (TokenException e) {
            // Refresh 토큰이 유효하지 않거나 만료된 경우
            throw new TokenException(AuthenticationError.REFRESH_TOKEN_NOT_VALID);
        }

        // refresh token redis 에 저장되어있는지도 확인
        if (!refreshTokenService.validateExist(refreshToken)) {
            throw new TokenException(AuthenticationError.REFRESH_TOKEN_EXPIRED);
        }

        // refresh 토큰으로 access 토큰 재발급하기
        return generateAccessToken(getAuthentication(refreshToken));
    }

    public String generateAccessToken(Authentication authentication) {

        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String generateRefreshToken(Authentication authentication) {
        // refresh 토큰 생성
        String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
        // 현재 로그인 중인 member 아이디로 redis에 저장
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        refreshTokenService.saveOrUpdate(principal.member().getId(), refreshToken);
        return refreshToken;
    }

    public void validateToken(String token) {

//        log.info("Validating token: {}", token);
        if (!StringUtils.hasText(token)) {
            log.warn("Token is null or empty.");
            throw new TokenException(AuthenticationError.TOKEN_NOT_VALID);
        }

        try {
            Claims claims = parseClaims(token);
            boolean isValid = claims.getExpiration().after(new Date());

            if (!isValid) {
                throw new TokenException(AuthenticationError.TOKEN_EXPIRED);
            }
        } catch (ExpiredJwtException e) {
            log.warn("Token is expired: {}", e.getMessage());
            throw new TokenException(AuthenticationError.TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            log.warn("Token is malformed: {}", e.getMessage());
            throw new TokenException(AuthenticationError.TOKEN_NOT_VALID);
        } catch (SecurityException e) {
            log.warn("Token signature is invalid: {}", e.getMessage());
            throw new TokenException(AuthenticationError.INVALID_JWT_SIGNATURE);
        } catch (TokenException e) {
            throw e; // Re-throw already caught TokenExceptions
        } catch (Exception e) {
            log.error("Unexpected error during token validation: {}", e.getMessage(), e);
            throw new TokenException(AuthenticationError.TOKEN_NOT_VALID);
        }
    }

    private String generateToken(Authentication authentication, long expireTime) {

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining());

        String generatedToken = Jwts.builder()
            .subject(authentication.getName())
            .claim(KEY_ROLE, authorities)
            .claim(KEY_MEMBER_ID,
                ((PrincipalDetails) authentication.getPrincipal()).member().getId())
            .issuedAt(now)
            .expiration(expiredDate)
            .signWith(secretKey, SIG.HS512)
            .compact();

        return generatedToken;
    }

    public void invalidateRefreshToken(HttpServletRequest request) {

        String refreshToken = CookieUtil.resolveRefreshTokenFromCookie(request);

        refreshTokenService.delete(refreshToken);
    }

    public Authentication getAuthentication(String token) {

        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        Long memberId = claims.get(KEY_MEMBER_ID, Long.class);
        String email = claims.getSubject();
        String roleString = claims.get(KEY_ROLE, String.class);
        Role role = Role.valueOf(roleString);

        Member member = Member.builder()
            .id(memberId)
            .email(email)
            .role(role)
            .build();

        // PrincipalDetails 객체 생성
        PrincipalDetails principalDetails = new PrincipalDetails(member, null,
            null);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails,
            token, authorities);
//        log.info("Authentication object created: {}", authentication);
        return authentication;
    }

    @Override
    public Authentication login(LoginRequest loginRequest) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            loginRequest.email(),
            loginRequest.password());
        return authenticationManager.authenticate(authentication);
    }

    @Override
    @Transactional
    public void sendPasswordResetMail(SendPasswordResetRequest request) {

        // 1) 해당 이메일이 존재하는지 확인 (없어도 에러 X, 200 OK)
        Member member = memberMapper.findByEmail(request.email())
            .orElse(null);
        if (member == null) {
            // 존재 여부 노출 방지: 동일한 응답 타이밍을 맞추고 바로 종료
            log.info("[PasswordReset] Non-existing email requested: {}", request.email());
            return;
        }

        // 2) SecureRandom 기반 32바이트 랜덤 토큰 생성
        String token = TokenUtil.generateRandomToken();

        // 3) 비밀번호 초기화 페이지 URL 생성
        String resetUrl = frontendBaseUrl + "/reset/password?token=" + token;

        // 4) redis에 토큰 저장
        passwordResetTokenRepository.saveToken(token, member.getId());

        // 5) SMTP 전송 (간단한 텍스트 메일)
        mailService.sendPasswordReset(request.email(), member.getName(), resetUrl);
    }

    @Override
    public void validatePwResetToken(PwResetTokenRequest request) {

        boolean isExist = passwordResetTokenRepository.exist(request.token());
        if (!isExist) {
            throw new IllegalStateException("Password reset token이 존재하지 않습니다.");
        }
    }

    @Override
    @Transactional
    public void ResetPassword(PasswordResetRequest request) {

        long memberId = passwordResetTokenRepository.findMemberIdByToken(request.token());
        memberService.updatePassword(memberId, request.password());
        passwordResetTokenRepository.deleteToken(request.token());
    }

    /**
     * jwt claims로 authorities 가져오기
     *
     * @param claims
     * @return
     */
    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {

        return Collections.singletonList(new SimpleGrantedAuthority(
            claims.get(KEY_ROLE).toString()));
    }

    /**
     * 토큰 파싱
     *
     * @param token
     * @return
     */
    private Claims parseClaims(String token) {

        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT exception during parsing: {}", e.getMessage());
            return e.getClaims();
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT exception during parsing: {}", e.getMessage());
            throw new TokenException(AuthenticationError.TOKEN_NOT_VALID);
        } catch (SecurityException e) {
            log.warn("Security exception (invalid signature) during parsing: {}", e.getMessage());
            throw new TokenException(AuthenticationError.INVALID_JWT_SIGNATURE);
        } catch (Exception e) {
            log.error("Unexpected exception during token parsing: {}", e.getMessage(), e);
            throw new TokenException(AuthenticationError.TOKEN_NOT_VALID);
        }
        // TODO: io.jsonwebtoken.security.SignatureException 추가하기
    }

}