package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.Friend;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.FriendDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static com.sdu.kob.consumer.WebSocketServer.goUsers;

@Service("FriendService")
public class FriendServiceImpl implements FriendService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private FriendDAO friendDAO;

    // userId 关注 follower 叫followed，follower关注userId叫follower
    @Override
    public String follow(String friendName, String userName) {
        User user = userDAO.findByUserName(userName);
        User friend = userDAO.findByUserName(friendName);
        Long userId = user.getId();
        Long friendId = friend.getId();
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
        Long userId = user.getId();
        Long friendId = friend.getId();
        friendDAO.update(userId, friendId, "false");
        return "success";
    }

    @Override
    public JSONObject getUserFollowed(String userName) {
        User user = userDAO.findByUserName(userName);
        Long userId = user.getId();
        List<Friend> followed = friendDAO.findByUserAAndFollowed(userId, "true");
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Friend friend : followed) {
            JSONObject item = new JSONObject();
            Long followedId = friend.getUserB();
            User u = userDAO.findById((long) followedId);
            item.put("id", u.getId());
            item.put("username", u.getUserName());
            item.put("state", checkLogin(u.getId()));
            item.put("avatar", u.getAvatar());
            item.put("level", u.getRating());
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
        Long userId = user.getId();
        List<Friend> followers = friendDAO.findByUserBAndFollowed(userId, "true");
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Friend friend : followers) {
            JSONObject item = new JSONObject();
            Long followedId = friend.getUserA();
            User u = userDAO.findById((long) followedId);
            item.put("id", u.getId());
            item.put("username", u.getUserName());
            item.put("state", checkLogin(u.getId()));
            item.put("avatar", u.getAvatar());
            item.put("level", u.getRating());
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
        Long userId = user.getId();
        List<Friend> followers = friendDAO.findByUserBAndFollowed(userId, "true");
        List<Friend> followed = friendDAO.findByUserAAndFollowed(userId, "true");

        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Friend a : followed) {
            Long userAId = a.getUserB();
            for (Friend b : followers) {
                if (b.getUserA().equals(userAId)) {
                    JSONObject item = new JSONObject();
                    User u = userDAO.findById((long) b.getUserA());
                    item.put("id", u.getId());
                    item.put("username", u.getUserName());
                    item.put("avatar", u.getAvatar());
                    item.put("state", checkLogin(u.getId()));
                    item.put("status", u.getStatus());
                    item.put("level", u.getRating());
                    item.put("win", u.getWin());
                    item.put("lose", u.getLose());
                    items.add(item);
                }
            }
        }
        resp.put("users", items);
        return resp;
    }

    @Override
    public JSONObject getRelationship(Long searchId, Long userId) {
        JSONObject resp = new JSONObject();
        Friend relationshipA = friendDAO.findByUserAAndUserB(userId, searchId);
        Friend relationshipB = friendDAO.findByUserAAndUserB(searchId, userId);
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
        return resp;
    }

    public static int checkLogin(Long id) {
        if (goUsers.containsKey(id)) return 1;
        return 0;
    }
}
