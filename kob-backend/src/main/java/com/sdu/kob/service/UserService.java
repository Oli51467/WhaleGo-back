package com.sdu.kob.service;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface UserService {
    JSONObject searchUser(String searchName, String userName);

    JSONObject getFollowedAndFollowersCountAndGuests(Integer userId, String userName);

    Map<String, String> updateUserInfo(Map<String, String> data);

    Map<String, String> updateUserPassword(String oldPassword, String newPassword, String confirmPassword);

}
