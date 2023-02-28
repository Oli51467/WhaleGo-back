package com.sdu.kob.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.response.BaseResponse;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@BaseResponse
public class BoardController {

    @Autowired
    private BoardService boardService;

    @RequestMapping(value = "/api/board/territory/", method = RequestMethod.GET)
    @ResponseBody
    public void getTerritory(@RequestParam Map<String, String> data) {
        String roomId = data.get("room_id");
        boardService.getTerritory(roomId);
    }
}
