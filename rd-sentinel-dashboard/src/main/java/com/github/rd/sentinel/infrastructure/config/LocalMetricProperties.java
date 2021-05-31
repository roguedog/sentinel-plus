package com.github.rd.sentinel.infrastructure.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("rd-sentinel-dashboard.local-metric")
@Slf4j
public class LocalMetricProperties {
    private int fetchIntervalSeconds;

    public int getFetchIntervalSeconds() {
        return fetchIntervalSeconds;
    }

    public void setFetchIntervalSeconds(int fetchIntervalSeconds) {
        if (fetchIntervalSeconds < 3 || fetchIntervalSeconds >8) {
            throw new IllegalArgumentException("3 <= rd-sentinel-dashboard.local-metric.fetch-interval-seconds >= 7");
        }
        this.fetchIntervalSeconds = fetchIntervalSeconds;
    }
}
