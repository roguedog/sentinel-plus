package com.github.rd.sentinel.infrastructure.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("rd-sentinel-dashboard.dashboard-service-manager")
@Slf4j
public class DashboardResourceManagerProperties {
    /**
     * 拉取指标的间隔
     */
    private int fetchMetricIntervalSeconds;

    public int getFetchMetricIntervalSeconds() {
        return fetchMetricIntervalSeconds;
    }

    public void setFetchMetricIntervalSeconds(int fetchMetricIntervalSeconds) {
        if (fetchMetricIntervalSeconds < 3 || fetchMetricIntervalSeconds >8) {
            throw new IllegalArgumentException("3 <= rd-sentinel-dashboard.local-metric.fetch-interval-seconds >= 7");
        }
        this.fetchMetricIntervalSeconds = fetchMetricIntervalSeconds;
    }
}
