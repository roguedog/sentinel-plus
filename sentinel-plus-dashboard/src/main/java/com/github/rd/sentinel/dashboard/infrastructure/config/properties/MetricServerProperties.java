package io.github.rd.sentinel.dashboard.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "metric-server")
public class MetricServerProperties {
    public static final String ENABLED_EXPRESSION = "${metric-server.enabled:false}";
    private boolean enabled;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private final PullMode pullMode = new PullMode();
    private final PushMode pushMode = new PushMode();

    public PullMode getPullMode() {
        return pullMode;
    }

    public PushMode getPushMode() {
        return pushMode;
    }

    public boolean isEnabled() {
        return enabled;
    }


    public static class PullMode {
        public static final String ENABLED_EXPRESSION = "${metric-server.pull-mode.enabled:false}";
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class PushMode {
        public static final String ENABLED_EXPRESSION = "${metric-server.push-mode.enabled:false}";
        private boolean enabled;

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }
}
