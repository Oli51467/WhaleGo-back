package com.sdu.kob.service;

import com.alibaba.fastjson.JSONObject;

public interface FriendService {
    String follow(String friendName, String userName);

    String unfollow(String friendName, String userName);

    JSONObject getUserFollowed(String userName);

    JSONObject getAllFollowers(String userName);

    JSONObject getFriends(String userName);

    JSONObject getRelationship(Integer searchId, Integer userId);
}
