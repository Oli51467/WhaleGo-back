package com.sdu.kob.controller;

import com.sdu.kob.service.ReceiveEnginePlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ReceiveEnginePlayController {

    @Autowired
    private ReceiveEnginePlayService receiveEnginePlayService;

    @RequestMapping(value = "/engine/receive/", method = RequestMethod.POST)
    public String receiveEnginePlay(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        String roomId = data.getFirst("room_id");
        Integer x = Integer.parseInt(Objects.requireNonNull(data.getFirst("next_x")));
        Integer y = Integer.parseInt(Objects.requireNonNull(data.getFirst("next_y")));
        return receiveEnginePlayService.receiveEnginePlay(userId, roomId, x, y);
    }

}
