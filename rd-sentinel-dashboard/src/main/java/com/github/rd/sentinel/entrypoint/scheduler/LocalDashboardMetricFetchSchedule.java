package com.github.rd.sentinel.entrypoint.scheduler;

import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.github.rd.sentinel.application.metric.LocalDashboardMetricFetcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class LocalDashboardMetricFetchSchedule implements ApplicationRunner {

    @Resource
    private LocalDashboardMetricFetcherService localDashboardMetricFetcherService;
    private ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(1, new NamedThreadFactory("lms"));

    @Override
    public void run(ApplicationArguments args) throws Exception {
        scheduleService.scheduleAtFixedRate(() -> {
            try {
                localDashboardMetricFetcherService.fetch();
            } catch (Exception e) {
                log.info("fetchLocalMetric error:", e);
            }
        }, 10, 10, TimeUnit.SECONDS);
        log.info("Local metric fetch schedule init");
    }

}
