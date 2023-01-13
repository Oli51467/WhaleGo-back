package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.Friend;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.FriendDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.service.FriendService;
import com.sdu.kob.utils.RatingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service("FriendService")
public class FriendServiceImpl implements FriendService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private FriendDAO friendDAO;

    @Autowired
    private SessionRegistry sessionRegistry;

    // userId 关注 follower 叫followed，follower关注userId叫follower
    @Override
    public String follow(String friendName, String userName) {
        User user = userDAO.findByUserName(userName);
        User friend = userDAO.findByUserName(friendName);
        Integer userId = user.getId();
        Integer friendId = friend.getId();
        Friend relationship = friendDAO.findByUserAAndUserB(userId, friendId);
        if (relationship == null) {
            Friend relation = new Friend(userId, friendId, "true");
            friendDAO.save(relation);
        } else {
            friendDAO.update(userId, friendId, "true");
        }
        return "success";
    }

    @Override
    public String unfollow(String friendName, String userName) {
        User user = userDAO.findByUserName(userName);
        User friend = userDAO.findByUserName(friendName);
        Integer userId = user.getId();
        Integer friendId = friend.getId();
        friendDAO.update(userId, friendId, "false");
        return "success";
    }

    @Override
    public JSONObject getUserFollowed(String userName) {
        User user = userDAO.findByUserName(userName);
        Integer userId = user.getId();
        List<Friend> followed = friendDAO.findByUserAAndFollowed(userId, "true");
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Friend friend : followed) {
            JSONObject item = new JSONObject();
            Integer followedId = friend.getUserB();
            User u = userDAO.findById((int) followedId);
            item.put("id", u.getId());
            item.put("username", u.getUserName());
            item.put("avatar", u.getAvatar());
            item.put("level", RatingUtil.getRating2Level(u.getRating()));
            item.put("win", u.getWin());
            item.put("lose", u.getLose());
            items.add(item);
        }
        resp.put("users", items);
        return resp;
    }

    @Override
    public JSONObject getAllFollowers(String userName) {
        User user = userDAO.findByUserName(userName);
        Integer userId = user.getId();
        List<Friend> followers = friendDAO.findByUserBAndFollowed(userId, "true");
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Friend friend : followers) {
            JSONObject item = new JSONObject();
            Integer followedId = friend.getUserA();
            User u = userDAO.findById((int) followedId);
            item.put("id", u.getId());
            item.put("username", u.getUserName());
            item.put("avatar", u.getAvatar());
            item.put("level", RatingUtil.getRating2Level(u.getRating()));
            item.put("win", u.getWin());
            item.put("lose", u.getLose());
            items.add(item);
        }
        resp.put("users", items);
        return resp;
    }

    @Override
    public JSONObject getFriends(String userName) {
        User user = userDAO.findByUserName(userName);
        Integer userId = user.getId();
        List<Friend> followers = friendDAO.findByUserBAndFollowed(userId, "true");
        List<Friend> followed = friendDAO.findByUserAAndFollowed(userId, "true");

        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Friend a : followed) {
            Integer userAId = a.getUserB();
            for (Friend b : followers) {
                if (b.getUserA().equals(userAId)) {
                    JSONObject item = new JSONObject();
                    User u = userDAO.findById((int) b.getUserA());
                    item.put("id", u.getId());
                    item.put("username", u.getUserName());
                    item.put("avatar", u.getAvatar());
                    item.put("state", checkLogin(u.getId()));
                    item.put("status", u.getStatus());
                    item.put("level", RatingUtil.getRating2Level(u.getRating()));
                    item.put("win", u.getWin());
                    item.put("lose", u.getLose());
                    items.add(item);
                }
            }
        }
        resp.put("users", items);
        return resp;
    }

    public int checkLogin(Integer id) {
        List<Object> list = sessionRegistry.getAllPrincipals();
        for (Object o : list) {
            if (o instanceof UserDetailsImpl) {
                if (((UserDetailsImpl) o).getUser().getId().equals(id)) {
                    return 1;
                }
            }
        }
        return 0;
    }
}
