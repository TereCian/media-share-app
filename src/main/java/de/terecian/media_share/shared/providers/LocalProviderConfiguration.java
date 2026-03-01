package de.terecian.media_share.shared.providers;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
@ComponentScan("de.terecian.media_share.local")
public class LocalProviderConfiguration {
}
