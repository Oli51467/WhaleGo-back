package com.sdu.kob.service;

import com.alibaba.fastjson.JSONObject;

public interface RecordService {
    JSONObject getRecordList(Integer page);
}
