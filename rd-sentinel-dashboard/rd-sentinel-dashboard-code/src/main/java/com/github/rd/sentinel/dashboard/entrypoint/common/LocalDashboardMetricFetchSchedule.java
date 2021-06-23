package com.github.rd.sentinel.dashboard.entrypoint.common;

import com.github.rd.sentinel.dashboard.application.resource.DashboardResourceManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class LocalDashboardMetricFetchSchedule implements ApplicationRunner {

    @Resource
    private DashboardResourceManager dashboardResourceManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        dashboardResourceManager.startFetchSchedule();
    }

}
