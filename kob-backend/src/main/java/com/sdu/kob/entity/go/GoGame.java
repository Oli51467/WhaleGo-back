package com.sdu.kob.entity.go;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.consumer.GoWebSocketServer;

import java.util.concurrent.locks.ReentrantLock;

import static com.sdu.kob.consumer.GoWebSocketServer.games;
import static com.sdu.kob.consumer.GoWebSocketServer.goUsers;

public class GoGame extends Thread {

    private final Player blackPlayer;
    private final Player whitePlayer;
    public final Board board;
    public Integer nextX = null, nextY = null;
    private String status = "playing";  // playing -> finished
    private Integer loser = null;
    private ReentrantLock lock = new ReentrantLock();

    public GoGame(Integer rows, Integer cols, Integer blackPlayerId, Integer whitePlayerId) {
        this.blackPlayer = new Player(1, blackPlayerId);
        this.whitePlayer = new Player(2, whitePlayerId);
        this.board = new Board(rows + 1, cols + 1, 0);
    }

    public Player getPlayer(int identifier) {
        return identifier == 1 ? blackPlayer : whitePlayer;
    }

    private void sendAllMessage(String message) {
        GoWebSocketServer clientA = goUsers.get(blackPlayer.getId());
        if (clientA != null) {
            clientA.sendMessage(message);
        } else {
            throw new NullPointerException("null user not found");
        }
        GoWebSocketServer clientB = goUsers.get(whitePlayer.getId());
        if (clientB != null) {
            clientB.sendMessage(message);
        } else {
            throw new NullPointerException("null user not found");
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
        for (int i = 0; i < 5000; i ++ ) {
            try {
                Thread.sleep(20);
                lock.lock();
                try {
                    if (this.nextX != null && this.nextY != null) {
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
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
            if (nextX == -1 && nextY == -1) {
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

    /**
     * 向两名玩家广播结果 根据两名玩家的id广播
     */
    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i ++ ) {
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
                games.remove(blackPlayer.getId());
                games.remove(whitePlayer.getId());
                break;
            }
        }
    }
}
