package com.sdu.kob.scheduler;

import com.sdu.kob.consumer.WebSocketServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling // 开启定时任务
public class StaticScheduler {

    @Scheduled(fixedRate = 55 * 1000) // 指定时间间隔
    private void configureTasks() throws Exception {
        WebSocketServer.sendGroupMessage("HeartBeat");
    }
}
