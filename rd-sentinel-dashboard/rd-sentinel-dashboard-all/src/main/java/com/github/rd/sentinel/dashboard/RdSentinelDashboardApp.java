package com.github.rd.sentinel.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan()
public class RdSentinelDashboardApp {
    public static void main(String[] args) {
        SpringApplication.run(RdSentinelDashboardApp.class, args);
    }
}
