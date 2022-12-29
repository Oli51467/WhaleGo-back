package com.sdu.bot.service.impl;

import com.sdu.bot.consumer.BotPool;
import com.sdu.bot.service.BotRunningService;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {

    public final static BotPool botPool = new BotPool();
    @Override
    public String addBot(Integer userId, String botCode, String input) {
        System.out.println("add bot:" + userId);
        botPool.addBot(userId, botCode, input);
        return "add bot success";
    }
}
