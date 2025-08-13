package org.jeayoung.template.planetbackend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jeayoung.template.planetbackend.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final UrlRewriteFilter urlRewriteFilter;


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() { // security를 적용하지 않을 리소스
        return web -> web.ignoring()
            .requestMatchers(
                "/error",  // Error Endpoint
                "/favicon.ico", // favicon
                "/h2-console/**", // h2 콘솔
                "/swagger-ui/**", // Swagger
                "/v3/api-docs/**" // Swagger
            );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // csrf 비활성화 (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 한다.)
            .csrf(AbstractHttpConfigurer::disable)
            // cors 활성화
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // 기본 인증 로그인 비활성화
            .httpBasic(AbstractHttpConfigurer::disable)
            // 기본 login form 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            // 기본 logout 비활성화
            .logout(AbstractHttpConfigurer::disable)
            // X-Frame-Options 비활성화
            .headers(c -> c.frameOptions(
                FrameOptionsConfig::disable))
            // 세션 사용하지 않음
            .sessionManagement(c ->
                c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // request 인증, 인가 설정
            .authorizeHttpRequests(request ->
                request
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(
                        new AntPathRequestMatcher("/"),
                        new AntPathRequestMatcher("/auth/success"),
                        new AntPathRequestMatcher("/auth/access-token/regenerate")
                    ).permitAll()
                    .anyRequest().authenticated()
            )

            // OAuth2 로그인 기능에 대한 여러 설정의 진입점
            .oauth2Login(oauth ->
                // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당
                oauth.userInfoEndpoint(c -> c.userService(oAuth2UserService))
                    .successHandler(oAuth2SuccessHandler) // 로그인 성공 시 핸들러
            )

            // JWT 관련 설정
            .addFilterBefore(urlRewriteFilter, OAuth2AuthorizationRequestRedirectFilter.class)
            .addFilterBefore(tokenAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class)

            // 토큰 예외 핸들링
            .addFilterAfter(new TokenExceptionFilter(objectMapper),
                TokenAuthenticationFilter.class)

            // 인증 예외 핸들링
            .exceptionHandling((exceptions) -> exceptions
                .authenticationEntryPoint(customAuthenticationEntryPoint) // 401
                .accessDeniedHandler(new AccessDeniedHandlerImpl()) // 403
            );

        return http.build();
    }
}