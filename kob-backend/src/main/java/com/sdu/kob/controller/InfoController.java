package com.sdu.kob.controller;

import com.sdu.kob.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class InfoController {

    @Autowired
    private InfoService infoService;

    @RequestMapping(value = "/api/account/info/", method = RequestMethod.GET)
    public Map<String, String> getInfo() {
        return infoService.getInfo();
    }
}
