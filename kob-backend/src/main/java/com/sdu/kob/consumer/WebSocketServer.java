package com.sdu.kob.consumer;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.User;
import com.sdu.kob.entity.Game;
import com.sdu.kob.repository.SnakeRecordDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    public static final String addPlayerUrl = "http://127.0.0.1:3001/matching/player/add/";
    public static final String removePlayerUrl = "http://127.0.0.1:3001/matching/player/remove/";

    // 存储所有user对应的连接 当匹配成功后，将匹配成功的连接返回给用户 加static为的是users对所有实例均可见
    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();

    private User user;
    private Session session = null; // 用户信息存储到session中

    private static UserDAO userDAO; // 用静态变量的set函数注入
    public static SnakeRecordDAO snakeRecordDAO;
    private Game game = null;

    private static RestTemplate restTemplate;   // 两个spring间通信的工具

    @Autowired
    public void setUserMapper(UserDAO userDAO) {
        WebSocketServer.userDAO = userDAO;
    }

    @Autowired
    public void setSnakeRecordMapper(SnakeRecordDAO snakeRecordDAO) {
        WebSocketServer.snakeRecordDAO = snakeRecordDAO;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接 所有与连接相关的信息都会存到这个类中
        this.session = session;
        // 1. 从token中读取建立连接的用户是谁 拿到id
        int userId = JwtUtil.JWTAuthentication(token);
        // 2. 根据id查找用户
        this.user = userDAO.findById(userId);
        System.out.println(user);
        // 3. 将用户存下来
        if (this.user != null) {
            users.put(userId, this);
            System.out.println("Connected!");
        } else {
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        // 关闭连接
        System.out.println("Closed!");
        if (this.user != null) {
            users.remove(this.user.getId());
        }
    }

    // *** 又匹配系统匹配出的结果 匹配出来两名玩家进行对战 ***
    public static void startGame(Integer aId, Integer bId) {
        User a = userDAO.findById((int) aId);
        User b = userDAO.findById((int) bId);

        Game game = new Game(19, 20, 55, a.getId(), b.getId());
        game.createMap();
        // 将同步的地图同步给两名玩家
        users.get(a.getId()).game = game;
        users.get(b.getId()).game = game;

        game.start();

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayer(1).getId());
        respGame.put("a_sx", game.getPlayer(1).getSx());
        respGame.put("a_sy", game.getPlayer(1).getSy());
        respGame.put("b_id", game.getPlayer(2).getId());
        respGame.put("b_sx", game.getPlayer(2).getSx());
        respGame.put("b_sy", game.getPlayer(2).getSy());
        respGame.put("map", game.getG());

        // A回传B的信息
        JSONObject respA = new JSONObject();
        respA.put("event", "start");
        respA.put("opponent_username", b.getUserName());
        respA.put("opponent_avatar", b.getAvatar());
        respA.put("game", respGame);
        // 用users哈希表获取A是哪个用户
        WebSocketServer userA = users.get(a.getId());
        if (userA != null) {
            userA.sendMessage(respA.toJSONString());
        } else {
            throw new NullPointerException("null user not found");
        }

        // B回传A的信息
        JSONObject respB = new JSONObject();
        respB.put("event", "start");
        respB.put("opponent_username", a.getUserName());
        respB.put("opponent_avatar", a.getAvatar());
        respB.put("game", respGame);
        // 用users哈希表获取B是哪个用户
        WebSocketServer userB = users.get(b.getId());
        if (userB != null) {
            userB.sendMessage(respB.toJSONString());
        } else {
            throw new NullPointerException("null user not found");
        }
    }

    private void startMatching() {
        System.out.println("start Matching");
        // 向matching Server发出一个请求开始匹配
        MultiValueMap<String, String> matchData = new LinkedMultiValueMap<>();
        matchData.add("user_id", this.user.getId().toString());
        matchData.add("rating", this.user.getRating().toString());
        restTemplate.postForObject(addPlayerUrl, matchData, String.class);
    }

    private void cancelMatching() {
        System.out.println("cancel Matching");
        // 向matching Server发出一个请求取消匹配
        MultiValueMap<String, String> cancelMatchData = new LinkedMultiValueMap<>();
        cancelMatchData.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, cancelMatchData, String.class);
    }

    private void move(int direction) {
        // 先判断自己是谁
        if (game.getPlayer(1).getId().equals(this.user.getId())) {
            this.game.setNextStepA(direction);
        } else if (game.getPlayer(2).getId().equals(this.user.getId())) {
            this.game.setNextStepB(direction);
        }
    }

    @OnMessage
    // 从Client接收消息 接收到前端信息时触发
    public void onMessage(String message, Session session) {    // 将message当作路由 根据请求作不同的处理
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start".equals(event)) {
            startMatching();
        } else if ("cancel".equals(event)) {
            cancelMatching();
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    // 从后端向前端发送信息
    public void sendMessage(String message) {
        // 每个连接用Session维护
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
