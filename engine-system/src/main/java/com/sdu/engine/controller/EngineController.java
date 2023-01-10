package com.sdu.engine.controller;

import com.sdu.engine.service.EngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class EngineController {
    @Autowired
    private EngineService engineService;

    @RequestMapping(value = "/engine/request/", method = RequestMethod.POST)
    public String addBot(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        String roomId = data.getFirst("room_id");
        return engineService.requestNextStep(userId, roomId);
    }
}
