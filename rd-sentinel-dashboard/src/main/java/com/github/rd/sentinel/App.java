package com.github.rd.sentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@Import(Config.class)
@EnableWebFlux
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
