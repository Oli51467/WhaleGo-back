package com.sdu.bot.consumer;

import com.sdu.bot.entity.Bot;
import com.sdu.bot.utils.BotInterface;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class JoorConsumer extends Thread {

    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/bot/move/receive/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        JoorConsumer.restTemplate = restTemplate;
    }

    public void startTimeout(long timeout, Bot bot) {
        this.bot = bot;
        this.start();

        try {
            this.join(timeout); // 最多执行timeout秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.interrupt();   // 中断当前线程
    }

    // 在code中类名后加uid
    private String addUid(String code, String uid) {
        int k = code.indexOf(" implements com.sdu.bot.utils.BotInterface");
        return code.substring(0, k) + uid + code.substring(k);
    }

    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);

        BotInterface botInterface = Reflect.compile(
                "com.sdu.bot.utils.BotCodeTest" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

        Integer direction = botInterface.nextMove(bot.getInput());
        System.out.println("bot generate next direction: " + direction);

        MultiValueMap<String, String> callbackData = new LinkedMultiValueMap<>();
        callbackData.add("user_id", bot.getUserId().toString());
        callbackData.add("direction", direction.toString());
        restTemplate.postForObject(receiveBotMoveUrl, callbackData, String.class);
    }
}
