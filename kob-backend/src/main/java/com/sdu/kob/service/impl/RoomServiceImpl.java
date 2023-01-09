package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.User;
import com.sdu.kob.entity.Room;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.service.RoomService;
import com.sdu.kob.utils.RatingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.sdu.kob.consumer.WebSocketServer.rooms;

@Service("RoomService")
public class RoomServiceImpl implements RoomService {

    @Autowired
    private UserDAO userDAO;

    /**
     * 返回roomId的房间中的所有用户
     * @param roomId 房间id
     * @return 房间的所有用户
     */
    @Override
    public JSONObject getUsersInRoom(String roomId) {
        JSONObject resp = new JSONObject();
        if (rooms.get(roomId) == null) {
            resp.put("event", "empty_room");
            return resp;
        }
        List<JSONObject> items = new LinkedList<>();
        for (Integer uid : rooms.get(roomId).users) {
            JSONObject item = new JSONObject();
            User user = userDAO.findById((int) uid);
            item.put("user_id", uid);
            item.put("user_name", user.getUserName());
            item.put("user_avatar", user.getAvatar());
            item.put("user_level", RatingUtil.getRating2Level(user.getRating()));
            item.put("user_lose", user.getLose());
            item.put("user_win", user.getWin());
            items.add(item);
        }
        resp.put("items", items);
        return resp;
    }

    @Override
    public JSONObject getBoardInRoom(Integer userId, String roomId) {
        JSONObject resp = new JSONObject();
        if (rooms.get(roomId) == null) {
            resp.put("event", "empty_room");
            return resp;
        }
        rooms.get(roomId).users.add(userId);
        Room room = rooms.get(roomId);
        resp.put("black_username", room.blackPlayer.getUser().getUserName());
        resp.put("black_id", room.blackPlayer.getId());
        resp.put("black_avatar", room.blackPlayer.getUser().getAvatar());
        resp.put("black_level", RatingUtil.getRating2Level(room.blackPlayer.getUser().getRating()));
        resp.put("black_win", room.blackPlayer.getUser().getWin());
        resp.put("black_lose", room.blackPlayer.getUser().getLose());

        resp.put("white_username", room.whitePlayer.getUser().getUserName());
        resp.put("white_id", room.whitePlayer.getId());
        resp.put("white_avatar", room.whitePlayer.getUser().getAvatar());
        resp.put("white_level", RatingUtil.getRating2Level(room.whitePlayer.getUser().getRating()));
        resp.put("white_win", room.whitePlayer.getUser().getWin());
        resp.put("white_lose", room.whitePlayer.getUser().getLose());
        resp.put("board_state", room.board.gameRecord.getLastTurn().boardState);
        return resp;
    }

    @Override
    public String leaveRoom(String roomId, Integer userId) {
        String msg = "";
        if (rooms.get(roomId).users.remove(userId)) {
            msg = "success";
        } else {
            msg =  "fail";
        }
        System.out.println(Arrays.toString(rooms.get(roomId).users.toArray()));
        return msg;
    }
}
