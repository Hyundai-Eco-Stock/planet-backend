package org.phoenix.planet.configuration.phti;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.ai.openai")
public class PhtiApiProperties {

    private List<String> apiKeys;
}
