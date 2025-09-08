package org.phoenix.planet.configuration.mybatis;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.phoenix.planet.constant.Role;
import org.phoenix.planet.constant.Sex;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {

        return configuration -> {

            configuration.getTypeHandlerRegistry().register(Role.class, new RoleTypeHandler());
            configuration.getTypeHandlerRegistry().register(Sex.class, new SexTypeHandler());
        };
    }
}