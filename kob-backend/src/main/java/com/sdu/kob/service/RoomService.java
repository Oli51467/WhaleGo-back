package com.sdu.kob.service;

import com.alibaba.fastjson.JSONObject;

public interface RoomService {
    JSONObject getUsersInRoom(String roomId);

    String leaveRoom(String roomId, Integer userId);

    JSONObject getBoardInRoom(Integer userId, String roomId);
}
