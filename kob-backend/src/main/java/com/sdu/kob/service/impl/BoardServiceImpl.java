package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.engine.EngineRequestImpl;
import com.sdu.kob.entity.Board;
import com.sdu.kob.entity.Point;
import com.sdu.kob.entity.Room;
import com.sdu.kob.response.ResponseCode;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.BoardService;
import com.sdu.kob.utils.BoardUtil;
import org.springframework.stereotype.Service;

import static com.sdu.kob.consumer.WebSocketServer.rooms;

@Service("BoardService")
public class BoardServiceImpl implements BoardService {

    @Override
    public void getTerritory(String roomId) {
        Room room = rooms.get(roomId);
        if (null == room) {
            System.out.println("null room");
            return;
//            return new ResponseResult(ResponseCode.ROOM_NOT_EXIST.getCode(), ResponseCode.ROOM_NOT_EXIST.getMsg(), null);
        }
        String req = room.playBoard.getState2Engine();
        EngineRequestImpl.requestTerritory(req);
        //return EngineRequestImpl.requestTerritory(req.toString());
//        int [][] territory = new int[20][20];
//        for (int x = 1; x <= 19; x ++ ) {
//            for (int y = 1; y <= 19; y ++ ) {
//                territory[x][y] = (int) (Math.random() * 100);
//            }
//        }
        //return new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), EngineRequestImpl.requestTerritory(req.toString()));
    }
}
