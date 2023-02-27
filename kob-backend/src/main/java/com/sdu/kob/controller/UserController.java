package com.sdu.kob.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.UserService;
import com.sdu.kob.service.impl.UserDetailsImpl;
import com.sdu.kob.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
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

    @RequestMapping(value = "/api/user/getFollowedAndFollowersCount/", method = RequestMethod.POST)
    public JSONObject getFollowedAndFollowersCountAndGuests(@RequestParam Map<String, String> data, Principal principal) {
        Long userId = Long.parseLong(data.get("user_id"));
        String requestUserName = principal.getName();
        return userService.getFollowedAndFollowersCountAndGuests(userId, requestUserName);
    }

    @RequestMapping(value = "/api/user/updateInfo/", method = RequestMethod.POST)
    public Map<String, String> updateUserUsername(@RequestParam Map<String, String> data) {
        return userService.updateUserInfo(data);
    }

    @RequestMapping(value = "/api/user/updatePassword/", method = RequestMethod.POST)
    public Map<String, String> updateUserPassword(@RequestParam Map<String, String> data) {
        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");
        String confirmPassword = data.get("confirmPassword");
        return userService.updateUserPassword(oldPassword, newPassword, confirmPassword);
    }

    @RequestMapping(value = "/api/user/update/avatar/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult updateUserAvatar(@RequestParam("file") MultipartFile[] file) {
        return userService.updateUserAvatar(file);
    }
}
