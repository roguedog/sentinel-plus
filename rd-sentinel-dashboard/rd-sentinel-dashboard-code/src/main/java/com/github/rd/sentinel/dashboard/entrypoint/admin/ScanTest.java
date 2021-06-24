package com.github.rd.sentinel.dashboard.entrypoint.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("scan")
public class ScanTest {

    @GetMapping
    public Object scan() {
        return "scan";
    }
}
