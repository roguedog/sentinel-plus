package com.github.rd.sentinel.dashboard.entrypoint.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * 应用和实例管理
 */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AppAdminApi {

    @GetMapping("/get")
    public Mono<String> get() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Mono.just("get");
    }

    @GetMapping("/get1")
    public Mono<String> get1() {
        return Mono.just("get");
    }
}