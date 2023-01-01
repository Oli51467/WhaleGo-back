package com.sdu.kob.service.impl;

import com.sdu.kob.consumer.WebSocketServer;
import com.sdu.kob.service.MatchService;
import org.springframework.stereotype.Service;

@Service("MatchService")
public class MatchServiceImpl implements MatchService {

    // 匹配系统返回结果的Service
    @Override
    public String startGame(Integer aId, Integer bId) {
        System.out.println("start game:" + aId + " " + bId);
        WebSocketServer.startGame(aId, bId);
        return "begin game success";
    }
}