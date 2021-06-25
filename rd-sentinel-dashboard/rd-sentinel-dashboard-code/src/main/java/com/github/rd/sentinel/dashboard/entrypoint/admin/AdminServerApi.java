package com.github.rd.sentinel.dashboard.entrypoint.admin;

import com.github.rd.sentinel.dashboard.infrastructure.config.properties.AdminServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 应用和实例管理
 */
@RestController
@RequestMapping("/admin")
@Slf4j
@ConditionalOnExpression(AdminServerProperties.ENABLED_EXPRESSION)
public class AdminServerApi {

    @GetMapping("")
    public Mono<String> admin() {
        return Mono.just("admin");
    }
}
