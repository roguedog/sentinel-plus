package com.github.rd.sentinel.dashboard.entrypoint.rule;

import com.github.rd.sentinel.dashboard.infrastructure.config.properties.RuleServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 规则拉取服务
 */
@RestController
@RequestMapping("/rule")
@Slf4j
@ConditionalOnExpression(RuleServerProperties.ENABLED_EXPRESSION)
public class RuleServerApi {

    @GetMapping("")
    public Mono<String> rule() {
        return Mono.just("rule");
    }
}
