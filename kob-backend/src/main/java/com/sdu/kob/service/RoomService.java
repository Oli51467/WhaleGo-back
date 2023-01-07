package com.sdu.kob.service;

import com.alibaba.fastjson.JSONObject;

public interface RoomService {
    JSONObject getUsersInRoom(String roomId, Integer userId);
}
