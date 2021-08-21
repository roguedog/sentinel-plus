package io.github.roguedog.sentinel.plus.example.entrypoint.admin;

import io.github.roguedog.sentinel.plus.example.infrastructure.config.properties.AdminServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 管理后台服务入口端点
 */
@Configuration
@ConditionalOnExpression(AdminServerProperties.ENABLED_EXPRESSION)
public class AdminServerEntryPoint {
    /**
     * 管理后台接口
     */
    @RestController
    @RequestMapping("/admin")
    @Slf4j
    public static class AdminServerApi {

        @GetMapping("")
        public Mono<String> admin() {
            return Mono.just("admin");
        }
    }

}
