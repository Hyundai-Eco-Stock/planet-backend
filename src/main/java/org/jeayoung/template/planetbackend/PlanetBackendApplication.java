package org.jeayoung.template.planetbackend;

import org.jeayoung.template.planetbackend.configuration.CookieProperties;
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
