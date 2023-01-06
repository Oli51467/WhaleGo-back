package com.sdu.kob.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GameController {

    @Autowired
    private GameService gameService;

    @RequestMapping(value = "/api/game/getInProcess/", method = RequestMethod.GET)
    public JSONObject getRecord() {
        return gameService.getGamesInProcess();
    }
}
