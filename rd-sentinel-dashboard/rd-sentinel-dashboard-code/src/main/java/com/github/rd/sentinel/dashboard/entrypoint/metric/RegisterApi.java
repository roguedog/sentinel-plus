package com.github.rd.sentinel.dashboard.entrypoint.metric;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务注册
 */
@RestController
@RequestMapping("/register")
public class RegisterApi {

    @GetMapping
    public Object register() {
        return "register";
    }
}
