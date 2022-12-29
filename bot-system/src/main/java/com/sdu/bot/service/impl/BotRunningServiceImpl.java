package com.sdu.bot.service.impl;

import com.sdu.bot.service.BotRunningService;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    @Override
    public String addBot(Integer userId, String botCode, String input) {
        System.out.println("add bot:" + userId + " " + botCode + " " + input);
        return "add bot success";
    }
}
