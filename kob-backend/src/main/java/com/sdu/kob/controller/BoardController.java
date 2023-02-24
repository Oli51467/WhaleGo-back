package com.sdu.kob.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BoardController {

    @Autowired
    private BoardService boardService;

    @RequestMapping(value = "/api/board/territory/", method = RequestMethod.GET)
    public JSONObject getTerritory(@RequestParam Map<String, String> data) {
        return boardService.getTerritory("1");
    }
}
