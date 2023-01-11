package com.sdu.kob.entity;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.consumer.WebSocketServer;
import com.sdu.kob.domain.Record;
import com.sdu.kob.domain.User;
import com.sdu.kob.entity.go.Board;
import com.sdu.kob.entity.go.GameTurn;
import com.sdu.kob.entity.go.Player;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;

import static com.sdu.kob.consumer.WebSocketServer.*;

public class Room extends Thread {

    public final String uuid;
    public final Player blackPlayer;
    public final Player whitePlayer;
    public final Board board;
    public Integer nextX = null, nextY = null;
    private String status = "playing";  // playing -> finished
    public String result = "";
    private Integer loser = null;
    private Integer humanId = null;
    public boolean isEngineTurn = true, hasEngine, isEngine;
    public int playCount;
    public CopyOnWriteArraySet<Integer> users;
    private ReentrantLock lock = new ReentrantLock();
    private static final String requestEngineUrl = "http://127.0.0.1:3002/engine/request/";

    public Room(Integer rows, Integer cols,
                Integer blackPlayerId, User blackUser,
                Integer whitePlayerId, User whiteUser, boolean hasEngine) {
        this.blackPlayer = new Player(1, blackPlayerId, blackUser); // 如果是引擎 那么blackPlayerId是-1
        this.whitePlayer = new Player(2, whitePlayerId, whiteUser);
        this.board = new Board(rows + 1, cols + 1, 0);
        this.uuid = UUID.randomUUID().toString().substring(0, 6);
        this.users = new CopyOnWriteArraySet<>();
        this.hasEngine = hasEngine;
        if (blackPlayerId != -1) {
            this.users.add(blackPlayerId);
        } else {
            this.humanId = whitePlayerId;
        }
        if (whitePlayerId != -1) {
            this.users.add(whitePlayerId);
        } else {
            this.humanId = blackPlayerId;
        }
        this.playCount = 0;
    }

    public Set<Integer> getUsers() {
        return users;
    }

    public String getStating() {
        lock.lock();
        try {
            if (this.playCount >= 0) {
                if (playCount <= 50) return "布局";
                else if (playCount <= 200) return "中盘";
                else return "官子";
            } else {
                return this.result;
            }
        } finally {
            lock.unlock();
        }
    }

    private void sendAllMessage(String message) {
        for (Integer usersInRoom : this.users) {
            WebSocketServer client = goUsers.get(usersInRoom);
            if (client != null) {
                client.sendMessage(message);
            } else {
                throw new NullPointerException("null user not found");
            }
        }
    }

    public void setNextStep(Integer x, Integer y, boolean isEngine) {
        lock.lock();
        try {
            this.nextX = x;
            this.nextY = y;
            this.isEngine = isEngine;
            if (isEngine) isEngineTurn = false;
        } finally {
            lock.unlock();
        }
    }

    public void setLoser(Integer loser) {
        this.loser = loser;
    }

    // 等待玩家的下一步操作
    public boolean nextStep() {
        while(true) {
            try {
                if (this.isInterrupted()) break;
                if (isEngineTurn && hasEngine) {
                    // 需要引擎来走这一步
                    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
                    data.add("user_id", this.humanId.toString());
                    data.add("room_id", this.uuid);
                    WebSocketServer.restTemplate.postForObject(requestEngineUrl, data, String.class);
                } else {
                    lock.lock();
                    try {
                        if (this.nextX != null && this.nextY != null) {
                            playCount++;
                            return true;
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            } finally {

            }
        }
        return false;
    }

    /**
     * 向两名Client传递移动信息
     */
    private void sendMove(boolean isValid) {
        String valid = "";
        if (isValid) valid = "yes";
        else valid = "no";
        JSONObject resp = new JSONObject();
        if (isValid) {
            GameTurn gameTurn = board.gameRecord.getLastTurn();
            resp.put("event", "play");
            resp.put("valid", valid);
            resp.put("board", gameTurn.boardState);
            resp.put("current", board.getPlayer().getIdentifier());
        } else {
            resp.put("event", "play");
            resp.put("valid", "no");
            resp.put("current", board.getPlayer().getIdentifier());
        }
        resp.put("room_id", uuid);
        nextX = nextY = null;
        sendAllMessage(resp.toJSONString());
    }

    // 判断落子是否合法
    private void judge() {
        Player curPlayer = board.getPlayer();
        lock.lock();
        try {
            if (nextX == -1 && nextY == -1 || nextX == -2 && nextY == -2) {
                this.status = "finished";
            }
            else if (board.play(nextX, nextY, curPlayer)) {
                board.nextPlayer();
                sendMove(true);
            } else {
                sendMove(false);
            }
            // 该引擎走
            if (this.isEngine) {
                isEngineTurn = false;
            } else {
                isEngineTurn = true;
            }
        } finally {
            lock.unlock();
        }
    }

    public void save2Database() {
        if (blackPlayer.getIdentifier().equals(loser)) {
            this.result = "白中盘胜";
        } else if (whitePlayer.getIdentifier().equals(loser)) this.result = "黑中盘胜";
        else this.result = "和棋";
        Record record = new Record(
                blackPlayer.getId(),
                whitePlayer.getId(),
                result,
                board.getSteps2Sgf(),
                null,
                new Date()
        );
        WebSocketServer.recordDAO.save(record);
    }

    /**
     * 向两名玩家广播结果 根据两名玩家的id广播
     */
    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        if (loser == -2) {
            resp.put("loser", "draw");
        }
        save2Database();
        resp.put("loser", result);
        sendAllMessage(resp.toJSONString());
        user2room.remove(this.blackPlayer.getId());
        user2room.remove(this.whitePlayer.getId());
        this.playCount = -1;
    }

    @Override
    public void run() {
        while (true) {
            if (this.isInterrupted()) break;
            if (nextStep()) {
                judge();
                if (this.status.equals("finished")) {
                    sendResult();
                    break;
                }
            } else {        // 给定时间内没有检测到落子
                status = "finished";
                Player player = board.getPlayer();
                if (player.getIdentifier() == 1) {
                    loser = blackPlayer.getId();
                } else {
                    loser = whitePlayer.getId();
                }
                sendResult();

                // 对局结束 移除保存的棋盘信息
                break;
            }
        }
        try {
            Thread.sleep(1000 * 60 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rooms.remove(this.uuid);
        }
    }
}
