package io.github.rd.sentinel.dashboard.entrypoint.server.rule;

import io.github.rd.sentinel.dashboard.application.resource.DashboardResourceManager;
import io.github.rd.sentinel.dashboard.infrastructure.config.properties.RuleServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Configuration
@ConditionalOnExpression(RuleServerProperties.ENABLED_EXPRESSION)
public class RuleServerEntryPoint {
    /**
     * 暴露规则拉取API
     */
    @RestController
    @RequestMapping("/rule")
    @Slf4j
    public static class RuleServerApi {

        @GetMapping("")
        public Mono<String> rule() {
            return Mono.just("rule");
        }
    }

    /**
     * 启动规则拉取任务
     */
    @Component
    public static class MetricFetchSchedule implements ApplicationRunner {

        @Resource
        private DashboardResourceManager dashboardResourceManager;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            dashboardResourceManager.startFetchSchedule();
        }

    }
}
