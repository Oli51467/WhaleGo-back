package com.sdu.kob.service;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface UserService {
    JSONObject searchUser(String searchName, String userName);

    String follow(String friendName, String userName);

    String unfollow(String friendName, String userName);
}
