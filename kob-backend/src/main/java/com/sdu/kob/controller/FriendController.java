package com.sdu.kob.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.service.FriendService;
import com.sdu.kob.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private RelationService relationService;

    @RequestMapping(value = "/api/friend/follow/", method = RequestMethod.POST)
    public String follow(@RequestParam Map<String, String> data, Principal principal) {
        String searchName = data.get("username");
        String userName = principal.getName();
        return friendService.follow(searchName, userName);
    }

    @RequestMapping(value = "/api/friend/unfollow/", method = RequestMethod.POST)
    public String unfollow(@RequestParam Map<String, String> data, Principal principal) {
        String searchName = data.get("username");
        String userName = principal.getName();
        return friendService.unfollow(searchName, userName);
    }

    @RequestMapping(value = "/api/friend/getFollowed/", method = RequestMethod.GET)
    public JSONObject getUserFollowed(Principal principal) {
        String userName = principal.getName();
        return friendService.getUserFollowed(userName);
    }

    @RequestMapping(value = "/api/friend/getFollowers/", method = RequestMethod.GET)
    public JSONObject getAllFollowers(Principal principal) {
        String userName = principal.getName();
        return friendService.getAllFollowers(userName);
    }

    @RequestMapping(value = "/api/friend/get/", method = RequestMethod.GET)
    public JSONObject getFriends(Principal principal) {
        String userName = principal.getName();
        return friendService.getFriends(userName);
    }

    @RequestMapping(value = "/api/friend/getRelationship/", method = RequestMethod.GET)
    public String getUserRelationship(@RequestParam Map<String, String> data) {
        Long searchId = Long.valueOf(data.get("search_id"));
        Long userId = Long.valueOf(data.get("user_id"));
        return relationService.getRelationship(searchId, userId);
    }
}
