package org.phoenix.planet.configuration.security;

import java.security.SecureRandom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeans {

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecureRandom secureRandom() {
        // OS 의 강력한 난수 소스를 사용하는 SecureRandom 인스턴스 생성
        return new SecureRandom();
    }
}