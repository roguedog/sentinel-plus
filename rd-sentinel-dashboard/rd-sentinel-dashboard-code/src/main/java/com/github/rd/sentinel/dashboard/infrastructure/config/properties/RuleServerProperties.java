package com.github.rd.sentinel.dashboard.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rd-sentinel-dashboard.rule-server")
public class RuleServerProperties {
    public static final String ENABLED_EXPRESSION = "${rd-sentinel-dashboard.rule-server.enabled:false}";
    private boolean enabled;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
