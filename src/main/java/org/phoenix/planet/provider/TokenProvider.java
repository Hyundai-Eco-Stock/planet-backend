package org.phoenix.planet.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
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
import org.phoenix.planet.error.auth.TokenException;
import org.phoenix.planet.service.RefreshTokenService;
import org.phoenix.planet.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${jwt.key}")
    private String key;

    @Value("${jwt.access-token-expire-millis}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.refresh-token-expire-millis}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    private SecretKey secretKey;

    private final String KEY_ROLE = "role";
    private final String KEY_MEMBER_ID = "memberId";

    private final RefreshTokenService refreshTokenService;

    @PostConstruct
    private void setSecretKey() {

        secretKey = Keys.hmacShaKeyFor(key.getBytes());
        log.info("JWT Secret Key initialized.");
    }

    /**
     * refresh 토큰을 이용하여 Access 토큰 재발급
     *
     * @param refreshToken
     * @return
     */
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

    /**
     * Access 토큰 발급
     *
     * @param authentication
     * @return
     */
    public String generateAccessToken(Authentication authentication) {

        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    /**
     * Refresh 토큰 발급 & Redis에 저장
     *
     * @param authentication
     * @return
     */
    public String generateRefreshToken(Authentication authentication) {
        // refresh 토큰 생성
        String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
        // 현재 로그인 중인 member 아이디로 redis에 저장
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        refreshTokenService.saveOrUpdate(principal.member().getId(), refreshToken);
        return refreshToken;
    }

    /**
     * 토큰 만료기간이 아직 도달하지 않았는지 여부 확인
     *
     * @param token
     * @return
     */
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

    /**
     * refresh/access 토큰 생성
     *
     * @param authentication
     * @param expireTime
     * @return
     */
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

//        log.info("Generated token for subject: {}, issuedAt: {}, expiresAt: {}",
//            authentication.getName(), now, expiredDate);
        return generatedToken;
    }

    public void invalidateRefreshToken(HttpServletRequest request) {

        String refreshToken = CookieUtil.resolveRefreshTokenFromCookie(request);

        refreshTokenService.delete(refreshToken);
    }

    public Authentication getAuthentication(String token) {

//        log.info("Getting authentication for token: {}", token);
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

//        log.info("Claims: subject={}, role={}", claims.getSubject(), claims.get(KEY_ROLE));
//        log.info("Authorities: {}", authorities);

        // Member 객체 생성 (DB 조회 없이)
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
        log.info("Authentication object created: {}", authentication);
        return authentication;
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
    }

}