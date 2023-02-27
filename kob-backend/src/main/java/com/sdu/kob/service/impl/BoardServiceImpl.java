package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.entity.Board;
import com.sdu.kob.entity.Room;
import com.sdu.kob.response.ResponseCode;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.BoardService;
import org.springframework.stereotype.Service;

import static com.sdu.kob.consumer.WebSocketServer.rooms;

@Service("BoardService")
public class BoardServiceImpl implements BoardService {
    @Override
    public ResponseResult getTerritory(String roomId) {
        //Room room = rooms.get(roomId);
//        if (null == room) {
//            return new ResponseResult(ResponseCode.ROOM_NOT_EXIST.getCode(), ResponseCode.ROOM_NOT_EXIST.getMsg(), null);
//        }
        //Board board = room.playBoard;

        int [][] territory = new int[20][20];
        for (int x = 1; x <= 19; x ++ ) {
            for (int y = 1; y <= 19; y ++ ) {
                territory[x][y] = (int) (Math.random() * 100);
            }
        }
        return new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), territory);
    }
}
