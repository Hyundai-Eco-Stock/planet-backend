package org.phoenix.planet.configuration.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.phoenix.planet.constant.AuthenticationError;
import org.phoenix.planet.constant.TokenKey;
import org.phoenix.planet.error.TokenException;
import org.phoenix.planet.provider.TokenProvider;
import org.phoenix.planet.service.RefreshTokenService;
import org.phoenix.planet.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    private final TokenProvider tokenProvider;

    private final RefreshTokenService refreshTokenService;

    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();
        path = path.replaceFirst("/api", "");

        return path.equals("/auth/access-token/regenerate") || path.startsWith(
            "/oauth2/authorization");
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

//        log.info("--- Incoming Request Headers ---");
//        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
//            request.getHeaders(headerName).asIterator().forEachRemaining(headerValue -> {
//                log.info("{}: {}", headerName, headerValue);
//            });
//        });
//        log.info("--------------------------------");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = resolveAccessToken(request);

        try {
            // 1. Access Token 유효성 검사
            tokenProvider.validateToken(accessToken);
            setAuthentication(accessToken);
            filterChain.doFilter(request, response);
            return;
        } catch (TokenException e) {
            log.warn("Access Token validation failed. Checking Refresh Token. Error: {}",
                e.getError());

            // Determine the specific access token error
            AuthenticationError accessTokenError;
            if (e.getError() == AuthenticationError.TOKEN_EXPIRED) {
                accessTokenError = AuthenticationError.ACCESS_TOKEN_EXPIRED;
            } else {
                accessTokenError = AuthenticationError.ACCESS_TOKEN_NOT_VALID;
            }

            // 2. Access Token이 유효하지 않을 경우, Refresh Token 유효성 검사
            String refreshToken = CookieUtil.resolveRefreshTokenFromCookie(request);

            try {
                tokenProvider.validateToken(refreshToken);
                // Access, Refresh 토큰 모두 유효하지 않으면 예외 발생 (refresh token redis 에 저장되어있는지도 확인)
                if (!refreshTokenService.validateExist(refreshToken)) {
                    log.warn(
                        "Refresh Token does not exist in Redis. Setting REFRESH_TOKEN_EXPIRED.");
                    setErrorResponse(response, AuthenticationError.REFRESH_TOKEN_EXPIRED);
                    return;
                }

                log.warn(
                    "Refresh Token is valid. Access Token needs regeneration. Setting ACCESS_TOKEN_EXPIRED to trigger client regeneration.");
                // 3. Access Token은 만료되었지만 Refresh Token은 유효한 경우
                // 클라이언트가 토큰 재발급을 요청하도록 유도하기 위해 동일한 예외를 발생시킵니다.
                // 클라이언트 측 (apiClient.js)에서는 이 응답을 받아 /auth/access-token/regenerate 를 호출합니다.
                setErrorResponse(response, accessTokenError); // Use the specific access token error
                return;

            } catch (TokenException refreshException) {
                log.warn(
                    "Refresh Token is also invalid or missing. Setting specific refresh token error: {}",
                    refreshException.getError());
                // Determine the specific refresh token error
                AuthenticationError refreshTokenError;
                if (refreshException.getError() == AuthenticationError.TOKEN_EXPIRED) {
                    refreshTokenError = AuthenticationError.REFRESH_TOKEN_EXPIRED;
                } else {
                    refreshTokenError = AuthenticationError.REFRESH_TOKEN_NOT_VALID;
                }
                setErrorResponse(response, refreshTokenError);
                return;
            }
        }

    }

    /**
     * accessToken으로 Security Context 세팅하기
     *
     * @param accessToken
     */
    private void setAuthentication(String accessToken) {

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
//        log.info("Authentication object from TokenProvider: {}", authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
//        log.info("SecurityContextHolder updated. Current authentication: {}",
//            SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * 헤더에서 AUTHORIZATION 가져와 Access 토큰 추출
     *
     * @param request
     * @return
     */
    private String resolveAccessToken(HttpServletRequest request) {

        String header = request.getHeader("Authorization");
//        log.info("Header 'Authorization' value: [{}]", header);
//        log.info("Header string length: {}", header != null ? header.length() : 0);
        if (header == null || header.length() == 0) {
            return null;
        }
        String prefix = TokenKey.AUTHENTICATION_PREFIX.getValue();
        if (!header.startsWith(prefix)) {
            return null;
        }
        return header.substring(prefix.length());
    }

    private void setErrorResponse(HttpServletResponse response, AuthenticationError error)
        throws IOException {

        response.setStatus(error.getHttpStatus().value());
        response.setHeader(TokenKey.X_ERROR_CODE.getValue(), error.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", error.name());
        body.put("message", error.getValue());

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
