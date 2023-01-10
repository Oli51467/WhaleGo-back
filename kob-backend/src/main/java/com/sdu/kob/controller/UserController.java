package com.sdu.kob.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/api/user/get/", method = RequestMethod.GET)
    public JSONObject searchUser(@RequestParam Map<String, String> data, Principal principal) {
        String searchName = data.get("username");
        String userName = principal.getName();
        return userService.searchUser(searchName, userName);
    }

    @RequestMapping(value = "/api/user/follow/", method = RequestMethod.POST)
    public String follow(@RequestParam Map<String, String> data, Principal principal) {
        String searchName = data.get("username");
        String userName = principal.getName();
        return userService.follow(searchName, userName);
    }

    @RequestMapping(value = "/api/user/unfollow/", method = RequestMethod.POST)
    public String unfollow(@RequestParam Map<String, String> data, Principal principal) {
        String searchName = data.get("username");
        String userName = principal.getName();
        return userService.unfollow(searchName, userName);
    }

    @RequestMapping(value = "/api/user/getFollowed/", method = RequestMethod.GET)
    public JSONObject getUserFollowed(Principal principal) {
        String userName = principal.getName();
        return userService.getUserFollowed(userName);
    }

    @RequestMapping(value = "/api/user/getFollowers/", method = RequestMethod.GET)
    public JSONObject getAllFollowers(Principal principal) {
        String userName = principal.getName();
        return userService.getAllFollowers(userName);
    }

    @RequestMapping(value = "/api/user/getFriends/", method = RequestMethod.GET)
    public JSONObject getFriends(Principal principal) {
        String userName = principal.getName();
        return userService.getFriends(userName);
    }

    @RequestMapping(value = "/api/user/getFollowedAndFollowersCount/", method = RequestMethod.GET)
    public JSONObject getFollowedAndFollowersCount(Principal principal) {
        String userName = principal.getName();
        return userService.getFollowedAndFollowersCount(userName);
    }
}
