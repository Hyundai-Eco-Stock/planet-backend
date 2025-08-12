package org.jeayoung.template.planetbackend.configuration;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
            "http://localhost:5173",
            "http://127.0.0.1:5173",
            "https://spring-security-frontend.jaeyoung.store"
        ));
//        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of(
            "GET",
            "POST",
            "PUT",
            "PATCH",
            "DELETE",
            "OPTIONS"
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of(
            "Authorization",
            "Set-Cookie",
            "Location",
            "X-Error-Code"
        ));
        config.setAllowCredentials(true);
//        config.setMaxAge(Duration.ofHours(1)); // 뭐임?

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
