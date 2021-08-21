package io.github.roguedog.sentinel.plus.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan(includeFilters = {
//        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = CustomScanFilter.class)
//})
public class DashboardApp {
    public static void main(String[] args) {
        SpringApplication.run(DashboardApp.class, args);
    }
}
