package com.github.rd.sentinel.dashboard.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rd-sentinel-dashboard.admin-server")
public class AdminServerProperties {
    public static final String ENABLED_EXPRESSION = "${rd-sentinel-dashboard.admin-server.enabled:false}";
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }
}
