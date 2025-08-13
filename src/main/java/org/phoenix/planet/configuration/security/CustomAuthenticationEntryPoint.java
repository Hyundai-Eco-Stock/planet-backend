package org.phoenix.planet.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phoenix.planet.constant.AuthenticationError;
import org.phoenix.planet.constant.TokenKey;
import org.phoenix.planet.error.auth.TokenException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException, ServletException {

        log.error("CustomAuthenticationEntryPoint.commence called. Exception type: {}",
            authException.getClass().getName());
        if (authException instanceof TokenException) {
            log.error("TokenException caught in CustomAuthenticationEntryPoint: {}",
                ((TokenException) authException).getError().name());
            setErrorResponse(response, ((TokenException) authException).getError());
        } else {
            // Fallback for other AuthenticationExceptions
            log.error("Other AuthenticationException caught in CustomAuthenticationEntryPoint: {}",
                authException.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> body = new HashMap<>();
            body.put("errorCode", "UNAUTHORIZED");
            body.put("message", "Unauthorized access");

            response.getWriter().write(objectMapper.writeValueAsString(body));
        }
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
