package com.sdu.kob.service;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.utils.RatingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sdu.kob.consumer.WebSocketServer.rooms;

@Service("RoomService")
public class RoomServiceImpl implements RoomService {

    @Autowired
    private UserDAO userDAO;

    /**
     * 将userId的用户加入到 roomId的房间中 并返回该房间的所有用户
     * @param userId 请求的用户id
     * @param roomId 房间id
     * @return 房间的所有用户
     */
    @Override
    public JSONObject getUsersInRoom(String roomId, Integer userId) {
        JSONObject resp = new JSONObject();
        List<Integer> usersInRoom = rooms.get(roomId);
        usersInRoom.add(userId);
        for (Integer uid : usersInRoom) {
            JSONObject item = new JSONObject();
            User user = userDAO.findById((int)uid);
            item.put("user_id", uid);
            item.put("user_name", user.getUserName());
            item.put("user_avatar", user.getAvatar());
            item.put("user_level", RatingUtil.getRating2Level(user.getRating()));
            item.put("user_lose", user.getLose());
            item.put("user_win", user.getWin());
            resp.put("items", item);
        }
        return resp;
    }
}
