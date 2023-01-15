package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.entity.Player;
import com.sdu.kob.entity.Room;
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
            Player blackPlayer = room.blackPlayer, whitePlayer = room.whitePlayer;
            JSONObject game = new JSONObject();
            if (blackPlayer.getId() == -1 ){
                game.put("black_username", "AI");
                game.put("black_avatar", "");
                game.put("black_level", "9段");
            } else {
                game.put("black_username", blackPlayer.getUser().getUserName());
                game.put("black_avatar", blackPlayer.getUser().getAvatar());
                game.put("black_level", RatingUtil.getRating2Level(blackPlayer.getUser().getRating()));
            }
            if (whitePlayer.getId() == -1) {
                game.put("white_username", "AI");
                game.put("white_avatar", "");
                game.put("white_level", "9段");
            } else {
                game.put("white_username", whitePlayer.getUser().getUserName());
                game.put("white_avatar", whitePlayer.getUser().getAvatar());
                game.put("white_level", RatingUtil.getRating2Level(whitePlayer.getUser().getRating()));
            }
            game.put("state", rooms.get(room.uuid).getStating());
            game.put("id", room.uuid);
            games.add(game);
        }
        resp.put("games", games);
        return resp;
    }
}
