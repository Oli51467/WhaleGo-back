package com.sdu.kob.entity.go;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.consumer.WebSocketServer;
import com.sdu.kob.domain.Record;
import com.sdu.kob.domain.User;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import static com.sdu.kob.consumer.WebSocketServer.*;

public class GoGame extends Thread {

    public final String uuid;
    public final Player blackPlayer;
    public final Player whitePlayer;
    public final Board board;
    public Integer nextX = null, nextY = null;
    private String status = "playing";  // playing -> finished
    public String result = "";
    private Integer loser = null;
    public int playCount;
    private ReentrantLock lock = new ReentrantLock();

    public GoGame(Integer rows, Integer cols,
                  Integer blackPlayerId, User blackUser,
                  Integer whitePlayerId, User whiteUser) {
        this.blackPlayer = new Player(1, blackPlayerId, blackUser);
        this.whitePlayer = new Player(2, whitePlayerId, whiteUser);
        this.board = new Board(rows + 1, cols + 1, 0);
        this.uuid = UUID.randomUUID().toString().substring(0, 6);
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
        for (Integer usersInRoom : rooms.get(this.uuid).getUsers()) {
            WebSocketServer client = goUsers.get(usersInRoom);
            if (client != null) {
                client.sendMessage(message);
            } else {
                throw new NullPointerException("null user not found");
            }
        }
    }

    public void setNextStep(Integer x, Integer y) {
        lock.lock();
        try {
            this.nextX = x;
            this.nextY = y;
        } finally {
            lock.unlock();
        }
    }

    public void setLoser(Integer id) {
        this.loser = id;
    }

    // 等待玩家的下一步操作
    public boolean nextStep() {
        while(true) {
            try {
                if (this.isInterrupted()) break;
                lock.lock();
                try {
                    if (this.nextX != null && this.nextY != null) {
                        playCount ++;
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
        resp.put("loser", loser);
        save2Database();
        sendAllMessage(resp.toJSONString());
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
