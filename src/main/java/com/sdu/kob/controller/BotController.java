package com.sdu.kob.controller;

import com.sdu.kob.domain.Bot;
import com.sdu.kob.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class BotController {

    @Autowired
    private BotService botService;

    @RequestMapping(value = "/api/bot/add/", method = RequestMethod.POST)
    public Map<String, String> add(@RequestParam Map<String, String> data) {
        return botService.addBot(data);
    }

    @RequestMapping(value = "/api/bot/getBots/", method = RequestMethod.GET)
    public List<Bot> getUserBots() {
        return botService.getUserBots();
    }

    @RequestMapping(value = "/api/bot/remove/", method = RequestMethod.POST)
    public Map<String, String> removeBot(@RequestParam Map<String, String> data) {
        return botService.removeBot(data);
    }

    @RequestMapping(value = "/api/bot/update/", method = RequestMethod.POST)
    public Map<String, String> updateBot(@RequestParam Map<String, String> data) {
        return botService.updateBot(data);
    }


}
