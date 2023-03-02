package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.Friend;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.FriendDAO;
import com.sdu.kob.repository.MessageDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.response.ResponseCode;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static com.sdu.kob.service.impl.FriendServiceImpl.checkLogin;

@Service("MessageService")
public class MessageServiceImpl implements MessageService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private FriendDAO friendDAO;

    @Autowired
    private MessageDAO messageDAO;

    @Override
    public ResponseResult getFriendsAndMessages(Long userId) {
        List<Friend> followers = friendDAO.findByUserBAndFollowed(userId, "true");
        List<Friend> followed = friendDAO.findByUserAAndFollowed(userId, "true");
        JSONObject resp = new JSONObject();
        List<JSONObject> friends = new LinkedList<>();
        for (Friend a : followed) {
            Long userAId = a.getUserB();
            for (Friend b : followers) {
                if (b.getUserA().equals(userAId)) {
                    JSONObject item = new JSONObject();
                    User u = userDAO.findById((long) b.getUserA());
                    item.put("id", u.getId());
                    item.put("name", u.getUserName());
                    item.put("avatar", u.getAvatar());
                    item.put("isOnline", checkLogin(u.getId()));
                    item.put("messages", messageDAO.getFriendsMessages(userId, u.getId()));
                    friends.add(item);
                }
            }
        }
        resp.put("friends", friends);
        return new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), resp);
    }
}
