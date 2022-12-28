package com.sdu.kob.service.impl;

import com.sdu.kob.consumer.GoWebSocketServer;
import com.sdu.kob.service.MatchService;
import org.springframework.stereotype.Service;

@Service("MatchGoService")
public class GoMatchServiceImpl implements MatchService {

    // 匹配系统返回结果的Service
    @Override
    public String startGame(Integer aId, Integer bId) {
        System.out.println("start game:" + aId + " " + bId);
        GoWebSocketServer.startGame(aId, bId);
        return "begin game success";
    }
}
