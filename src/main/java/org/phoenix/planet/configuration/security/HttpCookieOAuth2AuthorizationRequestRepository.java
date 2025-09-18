package org.phoenix.planet.configuration.security;


import com.nimbusds.oauth2.sdk.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Slf4j
public class HttpCookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH2_AUTH_REQUEST_COOKIE_NAME = "OAUTH2_AUTH_REQ";
    public static final int COOKIE_EXPIRE_SECONDS = 180; // 3분

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return getCookie(request, OAUTH2_AUTH_REQUEST_COOKIE_NAME)
                .map(c -> {
                    log.info("[OAuth2] 쿠키에서 AuthorizationRequest 로드 성공. value(length)={}", c.getValue().length());
                    return deserialize(c.getValue());
                })
                .orElseGet(() -> {
                    log.warn("[OAuth2] 쿠키에서 AuthorizationRequest 없음");
                    return null;
                });
    }
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        if (authRequest == null) {
            log.info("[OAuth2] AuthorizationRequest 없음 → 쿠키 삭제");
            removeAuthorizationRequestCookies(response);
            return;
        }
        String value = serialize(authRequest);
        Cookie cookie = new Cookie(OAUTH2_AUTH_REQUEST_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_EXPIRE_SECONDS);
        cookie.setSecure(true);

        response.addCookie(cookie);
        log.info("[OAuth2] AuthorizationRequest 저장 → 쿠키 추가 완료. size={} bytes", value.length());
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        OAuth2AuthorizationRequest req = loadAuthorizationRequest(request);
        log.info("[OAuth2] AuthorizationRequest 제거 시도. 존재 여부={}", req != null);
        removeAuthorizationRequestCookies(response);
        return req;
    }

    public void removeAuthorizationRequestCookies(HttpServletResponse response) {
        Cookie cookie = new Cookie(OAUTH2_AUTH_REQUEST_COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        response.addCookie(cookie);
        log.info("[OAuth2] AuthorizationRequest 쿠키 삭제 완료");
    }

    private Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return Optional.empty();
        for (Cookie c : request.getCookies()) {
            if (name.equals(c.getName())) return Optional.of(c);
        }
        return Optional.empty();
    }

    private String serialize(OAuth2AuthorizationRequest obj) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    private OAuth2AuthorizationRequest deserialize(String s) {
        if (StringUtils.isBlank(s)) return null;
        return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(s));
    }
}