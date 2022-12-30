package com.sdu.kob.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RecordController {

    @Autowired
    private RecordService recordService;

    @RequestMapping(value = "/api/record/get/", method = RequestMethod.GET)
    public JSONObject getRecord(@RequestParam Map<String, String> data) {
        Integer page = Integer.parseInt(data.get("page"));
        return recordService.getRecordList(page);
    }
}
