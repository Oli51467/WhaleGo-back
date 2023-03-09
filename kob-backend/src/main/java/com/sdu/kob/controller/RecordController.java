package com.sdu.kob.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class RecordController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private UserDAO userDAO;

    @RequestMapping(value = "/api/record/getAll/", method = RequestMethod.GET)
    public JSONObject getRecord(@RequestParam Map<String, String> data, Principal principal) {
        Integer page = Integer.parseInt(data.get("page"));
        String userName = principal.getName();
        User user = userDAO.findByUserName(userName);
        Long userId = user.getId();
        return recordService.getAllRecords(userId, page);
    }

    @RequestMapping(value = "/api/record/getMy/", method = RequestMethod.GET)
    public JSONObject getMyRecords(@RequestParam Map<String, String> data) {
        Integer page = Integer.parseInt(data.get("page"));
        Long userId = Long.parseLong(data.get("user_id"));
        return recordService.getMyRecords(userId, page);
    }

    @RequestMapping(value = "/api/record/detail/", method = RequestMethod.GET)
    public JSONObject getRecordDetail(@RequestParam Map<String, String> data) {
        long recordId = Long.parseLong(data.get("record_id"));
        return recordService.getRecordDetails(recordId);
    }
}
