package com.sdu.kob.service;

import com.sdu.kob.response.ResponseResult;

public interface LoginService {
    ResponseResult getToken(String userName, String password);
}
