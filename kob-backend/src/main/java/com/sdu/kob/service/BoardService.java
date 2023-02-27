package com.sdu.kob.service;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.response.ResponseResult;

public interface BoardService {
    ResponseResult getTerritory(String roomId);
}
