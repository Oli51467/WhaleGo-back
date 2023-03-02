package com.sdu.kob.controller;

import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/api/messages/get/", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getFriendsAndMessages(@RequestParam Map<String, String> data) {
        Long userId = Long.parseLong(data.get("user_id"));
        return messageService.getFriendsAndMessages(userId);
    }
}
