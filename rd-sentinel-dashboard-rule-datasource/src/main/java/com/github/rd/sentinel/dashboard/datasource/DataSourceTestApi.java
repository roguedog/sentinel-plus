package com.github.rd.sentinel.dashboard.datasource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/datasource")
public class DataSourceTestApi {

    @GetMapping("")
    public Object test() {
        return "datasource";
    }
}
