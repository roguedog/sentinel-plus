package com.github.rd.sentinel.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com/github/rd/sentinel/dashboard"})
public class DashboardAllApp {
    public static void main(String[] args) {
        SpringApplication.run(DashboardAllApp.class, args);
    }
}
