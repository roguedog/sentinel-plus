package com.github.rd.sentinel.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScans({
        @ComponentScan(value = {
                "com.github.rd.sentinel.dashboard.entrypoint.admin",
                "com.github.rd.sentinel.dashboard.entrypoint.common",
                "com.github.rd.sentinel.dashboard.application",
                "com.github.rd.sentinel.dashboard.infrastructure"
        }, excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {
                        //排除类
                })
        })
})
public class DashboardAdminApp {
    public static void main(String[] args) {
        SpringApplication.run(DashboardAdminApp.class, args);
    }
}
