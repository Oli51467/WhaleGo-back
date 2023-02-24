package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.service.BoardService;
import org.springframework.stereotype.Service;

@Service("BoardService")
public class BoardServiceImpl implements BoardService {
    @Override
    public JSONObject getTerritory(String roomId) {

        int [][] territory = new int[20][20];
        for (int x = 1; x <= 19; x ++ ) {
            for (int y = 1; y <= 19; y ++ ) {
                territory[x][y] = (int) (Math.random() * 100);
            }
        }
        JSONObject resp = new JSONObject();
        resp.put("territory", territory);
        return resp;
    }
}
