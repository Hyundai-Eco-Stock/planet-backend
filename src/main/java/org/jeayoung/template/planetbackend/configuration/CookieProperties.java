package org.jeayoung.template.planetbackend.configuration;// CookieProperties.java

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cookie")
public record CookieProperties(
    String domain,
    String sameSite,
    boolean secure,
    boolean httpOnly
) {

}