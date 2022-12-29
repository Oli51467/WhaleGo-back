package com.sdu.bot.consumer;

import com.sdu.bot.entity.Bot;
import com.sdu.bot.utils.BotInterface;
import org.joor.Reflect;

import java.util.UUID;

public class JoorConsumer extends Thread {

    private Bot bot;

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

        System.out.println(botInterface.nextMove(bot.getInput()));
    }
}
