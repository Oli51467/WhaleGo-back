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

    @RequestMapping(value = "/api/messages/getCount/", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getMessagesCount(@RequestParam Map<String, String> data) {
        Long userId = Long.parseLong(data.get("user_id"));
        return messageService.getMessageCount(userId);
    }

    @RequestMapping(value = "/api/messages/getUnread/", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getUnreadMessageCount(@RequestParam Map<String, String> data) {
        Long sendId = Long.parseLong(data.get("send_id"));
        Long receiveId = Long.parseLong(data.get("receive_id"));
        return messageService.getPeer2PeerUnreadMessageCount(sendId, receiveId);
    }

    @RequestMapping(value = "/api/messages/clearUnread/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult clearUnreadMessageCount(@RequestParam Map<String, String> data) {
        Long sendId = Long.parseLong(data.get("send_id"));
        Long receiveId = Long.parseLong(data.get("receive_id"));
        return messageService.clearUnreadMessageCount(sendId, receiveId);
    }
}
