package com.sdu.kob.service;

import com.alibaba.fastjson.JSONObject;

public interface RecordService {
    JSONObject getAllRecords(Long userId, Integer page);

    JSONObject getMyRecords(Long userId, Integer page);

    JSONObject getRecordDetails(long recordId);
}
