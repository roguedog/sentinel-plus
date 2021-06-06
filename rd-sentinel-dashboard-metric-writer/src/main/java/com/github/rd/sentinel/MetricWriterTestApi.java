package com.github.rd.sentinel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/writer")
public class MetricWriterTestApi {

    @GetMapping("")
    public Object test() {
        return "writer";
    }
}
