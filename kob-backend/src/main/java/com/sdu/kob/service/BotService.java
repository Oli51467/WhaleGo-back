package com.sdu.kob.service;

import com.sdu.kob.domain.Bot;

import java.util.List;
import java.util.Map;

public interface BotService {
    Map<String, String> addBot(Map<String, String> data);

    List<Bot> getUserBots();

    Map<String, String> removeBot(Map<String, String> data);

    Map<String, String> updateBot(Map<String, String> data);
}
