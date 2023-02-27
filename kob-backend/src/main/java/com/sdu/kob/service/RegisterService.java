package com.sdu.kob.service;

import com.sdu.kob.response.ResponseResult;

public interface RegisterService {
    ResponseResult register(String userName, String password);
}
