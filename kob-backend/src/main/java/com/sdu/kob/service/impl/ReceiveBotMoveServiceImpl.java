package com.sdu.kob.service.impl;

import com.sdu.kob.consumer.WebSocketServer;
import com.sdu.kob.entity.Game;
import com.sdu.kob.service.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service("ReceiveBotMoveService")
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        System.out.println("receive:" + userId + " " + direction);
        if (WebSocketServer.users.get(userId) != null) {
            Game game = WebSocketServer.users.get(userId).game;
            if (game != null) {
                if (game.getPlayer(1).getId().equals(userId)) {
                    game.setNextStepA(direction);
                } else if (game.getPlayer(2).getId().equals(userId)) {
                    game.setNextStepB(direction);
                }
            }
        }
        return "receive bot move success";
    }
}
