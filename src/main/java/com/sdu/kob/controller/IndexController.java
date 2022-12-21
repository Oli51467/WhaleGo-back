package com.sdu.kob.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexController {
    @RequestMapping("/index")
    public Map<String, String> getInfo() {
        Map<String, String> result = new HashMap<>();
        result.put("name", "tiger");
        result.put("rating", "1500");
        return result;
    }
}
