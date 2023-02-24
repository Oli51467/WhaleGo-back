package com.sdu.kob.entity;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.common.GameStatus;
import com.sdu.kob.consumer.WebSocketServer;
import com.sdu.kob.domain.Record;
import com.sdu.kob.domain.User;
import com.sdu.kob.engine.EngineRequest;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;

import static com.sdu.kob.consumer.WebSocketServer.*;
import static com.sdu.kob.engine.EngineRequest.resign;
import static com.sdu.kob.entity.Board.BLACK;
import static com.sdu.kob.entity.Board.WHITE;
import static com.sdu.kob.utils.BoardUtil.getNext;
import static com.sdu.kob.utils.BoardUtil.getPositionByIndex;
import static com.sdu.kob.utils.RecordUtil.updateUserRecord;

public class Room extends Thread {

    public final String uuid;
    public final Player blackPlayer;
    public final Player whitePlayer;
    public final Board playBoard;

    public Integer nextX = null, nextY = null;
    private GameStatus status = GameStatus.PLAYING;  // playing -> finished
    public String result = "";
    private Integer loser = null;
    private Long humanId = null;
    public boolean isEngineTurn = false, hasEngine;
    public int playCount;
    public CopyOnWriteArraySet<Long> users;
    private ReentrantLock lock = new ReentrantLock();

    public Room(Integer rows, Integer cols,
                Long blackPlayerId, User blackUser,
                Long whitePlayerId, User whiteUser, boolean hasEngine) {
        this.blackPlayer = new Player(BLACK, blackPlayerId, blackUser); // 如果是引擎 那么whitePlayerId是-1
        this.whitePlayer = new Player(WHITE, whitePlayerId, whiteUser);
        this.playBoard = new Board(rows, cols, 0);
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
        for (Long usersInRoom : this.users) {
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
            if (!isEngine) isEngineTurn = true;
        } finally {
            lock.unlock();
        }
    }

    public void setLoser(Integer loser) {
        this.loser = loser;
    }

    // 等待玩家的下一步操作
    public boolean nextStep() {
        while (true) {
            try {
                if (this.isInterrupted()) break;
                lock.lock();
                try {
                    if (this.nextX != null && this.nextY != null) {
                        playCount++;
                        return true;
                    }
                } finally {
                    lock.unlock();
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
            resp.put("event", "play");
            resp.put("valid", valid);
            resp.put("board", playBoard.board);
            resp.put("last_x", nextX);
            resp.put("last_y", nextY);
            resp.put("current", playBoard.player);
        } else {
            resp.put("event", "play");
            resp.put("valid", "no");
            resp.put("current", playBoard.player);
            resp.put("last_x", -1);
            resp.put("last_y", -1);
        }
        resp.put("room_id", uuid);
        nextX = nextY = null;
        sendAllMessage(resp.toJSONString());
    }

    public void regretPlay(Integer player) {
        JSONObject resp = new JSONObject();
        if (!this.playBoard.regretPlay(player)) {
            resp.put("event", "invalid_regret");
            sendAllMessage(resp.toJSONString());
            return;
        }
        resp.put("event", "regret_success");
        resp.put("valid", "yes");
        resp.put("board", playBoard.board);
        resp.put("last_x", playBoard.steps.peek().getX());
        resp.put("last_y", playBoard.steps.peek().getY());
        resp.put("current", playBoard.player);
        resp.put("room_id", uuid);
        sendAllMessage(resp.toJSONString());
    }

    // 判断落子是否合法
    private void judge() {
        lock.lock();
        try {
            if (nextX == -1 && nextY == -1 || nextX == -2 && nextY == -2) {
                this.status = GameStatus.FINISHED;
                if (hasEngine) {
                    resign(this.humanId.toString());
                }
            } else if (playBoard.play(nextX, nextY)) {
                Integer tmpX = nextX, tmpY = nextY;
                sendMove(true);
                // 需要引擎来走这一步
                if (hasEngine && isEngineTurn) {
                    isEngineTurn = false;
                    JSONObject resp = EngineRequest.requestNextStep(this.humanId.toString(), getPositionByIndex(tmpX, tmpY), playBoard.player);
                    System.out.println(resp);
                    if (resp != null && resp.getInteger("code") == 1000) {
                        String indexes = resp.getObject("data", JSONObject.class).getString("move");
                        if (!indexes.equals("pass")) {
                            this.nextX = getNext(indexes.substring(0, 1), indexes.substring(1))[0];
                            this.nextY = getNext(indexes.substring(0, 1), indexes.substring(1))[1];
                        }
                    }
                }
            } else {
                sendMove(false);
            }
        } finally {
            lock.unlock();
        }
    }

    public void save2Database() {
        if (blackPlayer.getIdentifier().equals(loser)) {
            this.result = "白中盘胜";
            updateUserRecord(whitePlayer, blackPlayer);
        } else if (whitePlayer.getIdentifier().equals(loser)) {
            this.result = "黑中盘胜";
            updateUserRecord(blackPlayer, whitePlayer);
        } else this.result = "和棋";
        Record record = new Record(
                blackPlayer.getId(),
                whitePlayer.getId(),
                result,
                playBoard.getSgf(),
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
                if (status == GameStatus.FINISHED) {
                    sendResult();
                    break;
                }
            } else {        // 给定时间内没有检测到落子
                status = GameStatus.FINISHED;
                if (playBoard.player == 1) {
                    loser = blackPlayer.getIdentifier();
                } else {
                    loser = whitePlayer.getIdentifier();
                }
                sendResult();

                // 对局结束 移除保存的棋盘信息
                break;
            }
        }
        try {
            Thread.sleep(1000 * 60 * 60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rooms.remove(this.uuid);
        }
    }
}
