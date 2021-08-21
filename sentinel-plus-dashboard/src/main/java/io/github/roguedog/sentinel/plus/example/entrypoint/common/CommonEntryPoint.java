package io.github.roguedog.sentinel.plus.example.entrypoint.common;

import io.github.roguedog.sentinel.plus.example.application.resource.DashboardResourceManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Configuration
public class CommonEntryPoint {

    @Component
    public static class LocalDashboardMetricFetchSchedule implements ApplicationRunner {

        @Resource
        private DashboardResourceManager dashboardResourceManager;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            dashboardResourceManager.startFetchSchedule();
        }

    }
}
