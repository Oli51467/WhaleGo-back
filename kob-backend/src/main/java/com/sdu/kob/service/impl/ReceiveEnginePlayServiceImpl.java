package com.sdu.kob.service.impl;

import com.sdu.kob.consumer.WebSocketServer;
import com.sdu.kob.entity.Room;
import com.sdu.kob.service.ReceiveEnginePlayService;
import org.springframework.stereotype.Service;

import static com.sdu.kob.consumer.WebSocketServer.rooms;

@Service("ReceiveEnginePlayService")
public class ReceiveEnginePlayServiceImpl implements ReceiveEnginePlayService {
    @Override
    public String receiveEnginePlay(Integer userId, String roomId, Integer x, Integer y) {
        System.out.println(userId + " " + roomId + " " + x + " " + y);
        if (WebSocketServer.goUsers.get(userId) != null) {
            Room room = rooms.get(roomId);
            if (room != null) {
                room.setNextStep(x, y, true);
            }
        }
        return "success";
    }
}
