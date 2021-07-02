package com.github.rd.sentinel.dashboard.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "admin-server")
public class AdminServerProperties {
    public static final String ENABLED_EXPRESSION = "${admin.enabled:false}";
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }
}
