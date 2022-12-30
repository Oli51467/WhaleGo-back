package com.sdu.kob.entity;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.consumer.WebSocketServer;
import com.sdu.kob.domain.Bot;
import com.sdu.kob.domain.SnakeRecord;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import static com.sdu.kob.consumer.WebSocketServer.users;

public class Game extends Thread {
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private static final int[] dx = {-1, 0, 1, 0};
    private static final int[] dy = {0, 1, 0, -1};

    private final Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;   // 存储两名玩家的下一步操作
    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing";  // playing -> finished
    private String loser = "";  // all: tied A: A lose B: B lose
    private static final String addBotUrl = "http://127.0.0.1:3002/bot/add/";

    public Game(Integer rows,
                Integer cols,
                Integer inner_walls_count,
                Integer idA,
                Bot botA,
                Integer idB,
                Bot botB
    ) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        Integer botIdA = 0, botIdB = 0;
        String botCodeA = "", botCodeB = "";
        if (botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if (botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        this.playerA = new Player(idA, botIdA, botCodeA, this.rows - 2, 1, new ArrayList<>());
        this.playerB = new Player(idB, botIdB, botCodeB, 1, this.cols - 2, new ArrayList<>());
    }

    public Player getPlayer(int identifier) {
        return identifier == 1 ? playerA : playerB;
    }

    // Client线程会通过通信修改这两个变量的值，next_step判定线程会读取这两个变量的值，涉及同步和加锁
    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    public int[][] getG() {
        return g;
    }

    private boolean checkConnectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (checkConnectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }

        g[sx][sy] = 0;
        return false;
    }

    private boolean draw() {  // 画地图
        for (int i = 0; i < this.rows; i ++ ) {
            for (int j = 0; j < this.cols; j ++ ) {
                g[i][j] = 0;
            }
        }

        for (int r = 0; r < this.rows; r ++ ) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c ++ ) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        Random random = new Random();
        for (int i = 0; i < this.inner_walls_count / 2; i ++ ) {
            for (int j = 0; j < 1000; j ++ ) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)
                    continue;
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                    continue;

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return checkConnectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for (int i = 0; i < 1000; i ++ ) {
            if (draw())
                break;
        }
    }

    // 辅助函数 获得当前局面 编码成字符串
    private String getInput(Player player) {
        // map#me.sx#me.sy#me.operation#oppo.sx#oppo.sy#oppo.sy#oppo.operation
        Player me, you;
        if (playerA.getId().equals(player.getId())) {
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }

        return getMap2String() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getSteps2String() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getSteps2String() + ")";
    }

    /**
     * 向bot执行系统发送代码
     * @param player 玩家
     */
    private void sendBotCode(Player player) {
        // 是人
        if (player.getBotId() == 0) return;
        // 否则需要执行一段代码
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    // 等待两名玩家的下一步操作
    public boolean nextStep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendBotCode(playerA);
        sendBotCode(playerB);
        for (int i = 0; i < 500; i ++ ) {
            try {
                Thread.sleep(200);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        // 记录两名玩家的操作
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
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

    private boolean checkValid(List<Cell> bodyA,List<Cell> bodyB) {
        int n = bodyA.size();
        Cell cell = bodyA.get(n - 1);   // 最新的一步
        if (g[cell.x][cell.y] == 1) return false;

        // 判断是否吃到了自己
        for (int i = 0; i < n - 1; i ++ ) {
            if (bodyA.get(i).x == cell.x && bodyA.get(i).y == cell.y)
                return false;
        }

        n = bodyB.size();

        // 判断是否吃到了对方
        for (int i = 0; i < n - 1; i ++ ) {
            if (bodyB.get(i).x == cell.x && bodyB.get(i).y == cell.y)
                return false;
        }
        return true;
    }

    /**
     * 判断玩家操作是否合法
     */
    private void judge() {
        List<Cell> bodyA = playerA.getCells();
        List<Cell> bodyB = playerB.getCells();
        // 判断A的移动是否合法
        boolean validA = checkValid(bodyA, bodyB);
        // 判断B的移动是否合法
        boolean validB = checkValid(bodyB, bodyA);
        if (!validA || !validB) {
            this.status = "finished";
            if (!validA && !validB) {
                loser = "all";
            } else if (!validA) {
                loser = "A";
            } else {
                loser = "B";
            }
        }
    }

    /**
     * 发送信息
     */
    private void sendAllMessage(String message) {
        WebSocketServer clientA = users.get(playerA.getId());
        if (clientA != null) {
            clientA.sendMessage(message);
        }
        WebSocketServer clientB = users.get(playerB.getId());
        if (clientB != null) {
            clientB.sendMessage(message);
        }
    }

    /**
     * 向两名Client传递移动信息
     */
    private void sendMove() {
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            // 进行下一步 先清空操作
            nextStepA = nextStepB = null;
            sendAllMessage(resp.toJSONString());
        } finally {
            lock.unlock();
        }
    }

    public String getMap2String() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < this.rows; i ++ ) {
            for (int j = 0; j < this.cols; j ++ ) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void save2Database() {
        SnakeRecord snakeRecord = new SnakeRecord(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getSteps2String(),
                playerB.getSteps2String(),
                getMap2String(),
                loser,
                new Date()
        );
        WebSocketServer.snakeRecordDAO.insert(snakeRecord);
    }


    /**
     * 向两名玩家广播结果 根据两名玩家的id广播
     */
    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        if (loser.equals("A")) {
            resp.put("a_direction", -1);
            resp.put("b_direction", nextStepB);
        } else if (loser.equals("B")) {
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", -1);
        } else if (loser.equals("all")) {
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
        }
        save2Database();
        sendAllMessage(resp.toJSONString());
    }

    private void removeAIFromUsers() {
        if (playerA.getId() < 0) {
            users.remove(playerA.getId());
        } else if (playerB.getId() < 0) {
            users.remove(playerB.getId());
        }
    }

    // 线程入口函数
    @Override
    public void run() {
        for (int i = 0; i < 1000; i ++ ) {
            if (nextStep()) {
                judge();
                if (status.equals("playing")) {
                    sendMove();
                } else {
                    sendResult();
                    break;
                }
            } else {
                status = "finished";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) {
                        loser = "all";
                        removeAIFromUsers();
                    } else if (nextStepA == null) {
                        loser = "A";
                        removeAIFromUsers();
                    } else {
                        loser = "B";
                        removeAIFromUsers();
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
