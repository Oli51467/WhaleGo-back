package com.sdu.kob.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RoomController {

    @Autowired
    private RoomService roomService;

    @RequestMapping(value = "/api/room/getUsers/", method = RequestMethod.GET)
    public JSONObject getUsersInRoom(@RequestParam Map<String, String> data) {
        String roomId = data.get("room_id");
        return roomService.getUsersInRoom(roomId);
    }

    @RequestMapping(value = "/api/room/leave/", method = RequestMethod.GET)
    public String leaveRoom(@RequestParam Map<String, String> data) {
        String roomId = data.get("room_id");
        Integer userId = Integer.parseInt(data.get("user_id"));
        System.out.println(roomId + " " + userId);
        return roomService.leaveRoom(roomId, userId);
    }

    @RequestMapping(value = "/api/room/getBoard/", method = RequestMethod.GET)
    public JSONObject getBoardInRoom(@RequestParam Map<String, String> data) {
        String roomId = data.get("room_id");
        Integer userId = Integer.parseInt(data.get("user_id"));
        return roomService.getBoardInRoom(userId, roomId);
    }
}
