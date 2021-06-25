package com.github.rd.sentinel.dashboard.entrypoint.metric;

import com.github.rd.sentinel.dashboard.infrastructure.config.properties.MetricServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务注册
 */
@RestController
@RequestMapping("/register")
@ConditionalOnExpression(MetricServerProperties.ENABLED_EXPRESSION)
public class RegisterApi {

    @GetMapping
    public Object register() {
        return "register";
    }
}
