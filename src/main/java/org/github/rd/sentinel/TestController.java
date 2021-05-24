package org.github.rd.sentinel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class TestController {

    @GetMapping("/get")
    public Object get() {
        return "hello";
    }
}
