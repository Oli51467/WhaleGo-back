package com.sdu.kob.service;

import com.alibaba.fastjson.JSONObject;

public interface RecordService {
    JSONObject getAllRecords(Integer userId, Integer page);

    JSONObject getMyRecords(Integer userId, Integer page);
}
