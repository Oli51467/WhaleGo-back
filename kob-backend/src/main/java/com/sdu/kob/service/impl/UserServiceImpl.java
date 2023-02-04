package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.Friend;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.FriendDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.service.UserService;
import com.sdu.kob.utils.JwtUtil;
import com.sdu.kob.utils.RatingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("UserService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private FriendDAO friendDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public JSONObject searchUser(String searchName, String userName) {
        User searchUser = userDAO.findByUserName(searchName);
        Integer userId = userDAO.findByUserName(userName).getId();  // 自己的id
        JSONObject resp = new JSONObject();
        if (searchUser == null) {
            resp.put("user", "none");
        } else {
            Friend relationshipA = friendDAO.findByUserAAndUserB(userId, searchUser.getId());
            Friend relationshipB = friendDAO.findByUserAAndUserB(searchUser.getId(), userId);
            if (relationshipA == null && relationshipB == null) {
                resp.put("relation", "stranger");
            } else if (relationshipA == null) {
                if (relationshipB.getFollowed().equals("false")) {
                    resp.put("relation", "stranger");
                } else if (relationshipB.getFollowed().equals("true")) {
                    resp.put("relation", "follower");
                }
            } else if (relationshipB == null) {
                if (relationshipA.getFollowed().equals("false")) {
                    resp.put("relation", "stranger");
                } else if (relationshipA.getFollowed().equals("true")) {
                    resp.put("relation", "followed");
                }
            } else {
                String ra = relationshipA.getFollowed(), rb = relationshipB.getFollowed();
                if (ra.equals("true") && rb.equals("true")) {
                    resp.put("relation", "friend");
                } else if (ra.equals("false") && rb.equals("true")) {
                    resp.put("relation", "follower");
                } else if (ra.equals("true") && rb.equals("false")) {
                    resp.put("relation", "followed");
                } else {
                    resp.put("relation", "stranger");
                }
            }
            resp.put("user", "exist");
            JSONObject item = new JSONObject();
            item.put("id", searchUser.getId());
            item.put("username", searchUser.getUserName());
            item.put("avatar", searchUser.getAvatar());
            item.put("level", RatingUtil.getRating2Level(searchUser.getRating()));
            resp.put("info", item);
        }
        return resp;
    }

    @Override
    public JSONObject getFollowedAndFollowersCountAndGuests(Integer userId, String userName) {
        User user = userDAO.findById((int) userId);
        User me = userDAO.findByUserName(userName);
        int followed = friendDAO.countByUserAAndFollowed(userId, "true");
        int followers = friendDAO.countByUserBAndFollowed(userId, "true");
        List<JSONObject> itemsFront = new LinkedList<>();
        List<JSONObject> itemsBack = new LinkedList<>();
        String guests = user.getGuests();
        LinkedList<String> clearIdx = new LinkedList<>(Arrays.asList(guests.split(";")));
        LinkedList<String> guestIds = new LinkedList<>();
        for (String idx : clearIdx) {
            if (idx != null && !idx.equals("")) guestIds.add(idx);
        }
        if (!userName.equals(user.getUserName())) {
            userDAO.updateGuestsCount(userId);
            for (int i = 0; i < guestIds.size(); i ++ ) {
                if (Integer.parseInt(guestIds.get(i)) == me.getId()) {
                    guestIds.remove(i);
                    break;
                }
            }
            if (guestIds.size() >= 12) {
                guestIds.remove(guestIds.size() - 1);
            }
            guestIds.add(0, me.getId().toString());
            StringBuffer sb = new StringBuffer();
            for (String guestId : guestIds) {
                sb.append(";").append(guestId);
            }
            userDAO.updateRecentGuests(userId, sb.toString());
        }
        int cnt = 0;
        for (String guestId : guestIds) {
            if (guestId.equals("")) continue;
            JSONObject item = new JSONObject();
            User guest = userDAO.findById(Integer.parseInt(guestId));
            item.put("guests_id", guest.getId().toString());
            item.put("guests_username", guest.getUserName());
            item.put("guests_avatar", guest.getAvatar());
            if (cnt <= 5) itemsFront.add(item);
            else itemsBack.add(item);
            cnt ++;
        }
        int guestsCount = user.getGuestsCount();
        JSONObject resp = new JSONObject();
        resp.put("followed_count", followed);
        resp.put("followers_count", followers);
        resp.put("guests_count", guestsCount);
        resp.put("username", user.getUserName());
        resp.put("id", user.getId());
        resp.put("user_avatar", user.getAvatar());
        resp.put("user_level", RatingUtil.getRating2Level(user.getRating()));
        resp.put("profile", user.getProfile());
        resp.put("guests_front", itemsFront);
        resp.put("guests_back", itemsBack);
        return resp;
    }

    @Override
    public Map<String, String> updateUserInfo(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = userDetails.getUser();

        String username = data.get("username");
        String profile = data.get("profile");

        Map<String, String> map = new HashMap<>();

        if (username == null || username.equals("")) {
            map.put("msg", "用户名不能为空");
            return map;
        }
        userDAO.updateUserInfo(user.getId(), username, profile);
        map.put("msg", "success");
        return map;
    }

    @Override
    public Map<String, String> updateUserPassword(String oldPassword, String newPassword, String confirmPassword) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = userDetails.getUser();
        boolean matches = passwordEncoder.matches(oldPassword, user.getPassword());
        Map<String, String> map = new HashMap<>();

        if (!matches) {
            map.put("msg", "原密码错误");
            return map;
        }
        if (newPassword == null || confirmPassword == null || newPassword.equals("") || confirmPassword.equals("")) {
            map.put("msg", "密码不能为空");
            return map;
        }
        if (newPassword.equals(oldPassword)) {
            map.put("msg", "新旧密码不能相同");
            return map;
        }
        if (!newPassword.equals(confirmPassword)) {
            map.put("msg", "两次输入的密码不同");
            return map;
        }

        String passwordEncode = passwordEncoder.encode(newPassword);
        userDAO.updateUserInfo(user.getId(), user.getUserName(), passwordEncode);
        String jwt = JwtUtil.createJWT(user.getId().toString());
        map.put("msg", "success");
        map.put("token", jwt);
        return map;
    }
}
