package com.github.rd.sentinel.dashboard.common.entrypoint.scheduler;

import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.github.rd.sentinel.dashboard.common.application.resource.DashboardResourceManager;
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
    private DashboardResourceManager dashboardResourceManager;
    private ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(1, new NamedThreadFactory("lms"));

    @Override
    public void run(ApplicationArguments args) throws Exception {
        scheduleService.scheduleAtFixedRate(() -> {
            try {
                dashboardResourceManager.fetchLocalMetric();
            } catch (Exception e) {
                log.info("fetchLocalMetric error:", e);
            }
        }, 3, dashboardResourceManager.getFetchLocalMetricPeriodSchedulerSeconds(), TimeUnit.SECONDS);
        log.info("Local metric fetch schedule init");
    }

}
