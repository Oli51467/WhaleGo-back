package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.Friend;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.FriendDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.service.UserService;
import com.sdu.kob.utils.RatingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("UserService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private FriendDAO friendDAO;

    @Override
    public JSONObject searchUser(String searchName, String userName) {
        User searchUser = userDAO.findByUserName(searchName);
        Integer userId = userDAO.findByUserName(userName).getId();
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

    // userId 关注 follower 叫followed，follower关注userId叫follower
    @Override
    public String follow(String friendName, String userName) {
        User user = userDAO.findByUserName(userName);
        User friend = userDAO.findByUserName(friendName);
        Integer userId = user.getId();
        Integer friendId = friend.getId();
        Friend relationship = friendDAO.findByUserAOrUserB(userId, friendId);
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
}
