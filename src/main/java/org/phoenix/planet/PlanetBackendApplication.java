package org.phoenix.planet;

import org.phoenix.planet.configuration.CookieProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CookieProperties.class)
public class PlanetBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(PlanetBackendApplication.class, args);
    }
}
