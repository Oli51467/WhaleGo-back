package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.entity.Room;
import com.sdu.kob.entity.go.GoGame;
import com.sdu.kob.entity.go.Player;
import com.sdu.kob.service.GameService;
import com.sdu.kob.utils.RatingUtil;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.sdu.kob.consumer.WebSocketServer.rooms;

@Service("GameService")
public class GameServiceImpl implements GameService {

    @Override
    public JSONObject getGamesInProcess() {
        JSONObject resp = new JSONObject();
        Set<Integer> usersId = new HashSet<>();
        for (Room room: rooms.values()) {
            Integer userId = room.getBlackPlayer();
            if (!usersId.contains(userId)) {
                usersId.add(userId);
                GoGame gameItem = room.getGoGame();
                Player blackPlayer = gameItem.blackPlayer, whitePlayer = gameItem.whitePlayer;
                Integer blackId = blackPlayer.getId();
                Integer whiteId = whitePlayer.getId();
                usersId.add(blackId);
                usersId.add(whiteId);
                JSONObject game = new JSONObject();
                game.put("black_username", blackPlayer.getUser().getUserName());
                game.put("black_avatar", blackPlayer.getUser().getAvatar());
                game.put("black_level", RatingUtil.getRating2Level(blackPlayer.getUser().getRating()));
                game.put("white_username", whitePlayer.getUser().getUserName());
                game.put("white_avatar", whitePlayer.getUser().getAvatar());
                game.put("white_level", RatingUtil.getRating2Level(whitePlayer.getUser().getRating()));
                game.put("state", room.getState());
                game.put("id", gameItem.uuid);
                resp.put("games", game);
            }
        }
        return resp;
    }

    private String getState(int count) {
        if (count <= 50) return "布局";
        else if (count <= 200) return "中盘";
        else return "官子";
    }
}
