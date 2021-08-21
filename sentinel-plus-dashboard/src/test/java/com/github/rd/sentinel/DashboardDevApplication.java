package io.github.rd.sentinel;

import io.github.roguedog.sentinel.plus.example.DashboardApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DashboardDevApplication {
    public static void main(String[] args) {
        SpringApplication.run(DashboardApp.class, args);
    }
}
