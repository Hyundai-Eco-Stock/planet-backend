package org.jeayoung.template.planetbackend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeayoung.template.planetbackend.constant.AuthenticationError;
import org.jeayoung.template.planetbackend.constant.TokenKey;
import org.jeayoung.template.planetbackend.error.TokenException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenExceptionFilter extends OncePerRequestFilter {


    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (TokenException e) {
            log.error("TokenException caught in TokenExceptionFilter: {}", e.getError().name());
            setErrorResponse(response, e.getError());
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
