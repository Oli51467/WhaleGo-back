package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.entity.Room;
import com.sdu.kob.entity.go.GoGame;
import com.sdu.kob.entity.go.Player;
import com.sdu.kob.service.GameService;
import com.sdu.kob.utils.RatingUtil;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static com.sdu.kob.consumer.WebSocketServer.rooms;

@Service("GameService")
public class GameServiceImpl implements GameService {

    @Override
    public JSONObject getGamesInProcess() {
        JSONObject resp = new JSONObject();
        List<JSONObject> games = new LinkedList<>();
        for (Room room: rooms.values()) {
            GoGame gameItem = room.getGoGame();
            Player blackPlayer = gameItem.blackPlayer, whitePlayer = gameItem.whitePlayer;
            JSONObject game = new JSONObject();
            game.put("black_username", blackPlayer.getUser().getUserName());
            game.put("black_avatar", blackPlayer.getUser().getAvatar());
            game.put("black_level", RatingUtil.getRating2Level(blackPlayer.getUser().getRating()));
            game.put("white_username", whitePlayer.getUser().getUserName());
            game.put("white_avatar", whitePlayer.getUser().getAvatar());
            game.put("white_level", RatingUtil.getRating2Level(whitePlayer.getUser().getRating()));
            game.put("state", room.getState());
            game.put("id", gameItem.uuid);
            games.add(game);
        }
        resp.put("games", games);
        return resp;
    }

    private String getState(int count) {
        if (count <= 50) return "布局";
        else if (count <= 200) return "中盘";
        else return "官子";
    }
}
