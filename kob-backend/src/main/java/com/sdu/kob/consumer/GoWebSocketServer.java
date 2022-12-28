package com.sdu.kob.consumer;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.User;
import com.sdu.kob.entity.go.GoGame;
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
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/go/websocket/{token}")
public class GoWebSocketServer {

    public static final String addPlayerUrl = "http://127.0.0.1:3001/go/matching/add/";
    public static final String removePlayerUrl = "http://127.0.0.1:3001/go/matching/remove/";

    final public static ConcurrentHashMap<Integer, GoWebSocketServer> goUsers = new ConcurrentHashMap<>();

    private User user;
    private Session session = null;
    private GoGame goGame = null;

    private static UserDAO userDAO;
    private static RestTemplate restTemplate;

    // temp
    private static Integer blackId, whiteId;

    @Autowired
    public void setUserMapper(UserDAO userDAO) {
        GoWebSocketServer.userDAO = userDAO;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        GoWebSocketServer.restTemplate = restTemplate;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接 所有与连接相关的信息都会存到这个类中
        this.session = session;
        // 1. 从token中读取建立连接的用户是谁 拿到id
        int userId = JwtUtil.JWTAuthentication(token);
        // 2. 根据id查找用户
        this.user = userDAO.findById(userId);
        System.out.println("go connect" + user);
        // 3. 将用户存下来
        if (this.user != null) {
            goUsers.put(userId, this);
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
            goUsers.remove(this.user.getId());
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
            System.out.println("Go game move!");
        } else if ("resign".equals(event)) {
            endGame();
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

    /**
     * 前端用户点击开始匹配
     */
    private void startMatching() {
        System.out.println("start go matching");
        MultiValueMap<String, String> matchData = new LinkedMultiValueMap<>();
        matchData.add("user_id", this.user.getId().toString());
        matchData.add("rating", this.user.getRating().toString());
        restTemplate.postForObject(addPlayerUrl, matchData, String.class);
    }

    /**
     * 用户点击取消匹配
     */
    private void cancelMatching() {
        System.out.println("cancel Matching");
        // 向matching Server发出一个请求取消匹配
        MultiValueMap<String, String> cancelMatchData = new LinkedMultiValueMap<>();
        cancelMatchData.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, cancelMatchData, String.class);
    }

    public static void startGame(Integer aId, Integer bId) {
        User a = userDAO.findById((int) aId);
        User b = userDAO.findById((int) bId);

        JSONObject respGame = new JSONObject();
        Random random = new Random();
        int seed = random.nextInt();
        if (seed * 10 >= 5) {
            blackId = aId;
            whiteId = bId;
        } else {
            blackId = bId;
            whiteId = aId;
        }
        respGame.put("black_id", blackId);
        respGame.put("white_id", whiteId);
        // A回传B的信息
        JSONObject respA = new JSONObject();
        respA.put("event", "start");
        respA.put("opponent_username", b.getUserName());
        respA.put("opponent_avatar", b.getAvatar());
        respA.put("game", respGame);
        // 用users哈希表获取A是哪个用户
        GoWebSocketServer userA = goUsers.get(a.getId());
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
        GoWebSocketServer userB = goUsers.get(b.getId());
        if (userB != null) {
            userB.sendMessage(respB.toJSONString());
        } else {
            throw new NullPointerException("null user not found");
        }
        System.out.println("ok");
    }

    // temp funx
    private void endGame() {
        JSONObject respGame = new JSONObject();
        respGame.put("event", "result");
        respGame.put("loser", this.user.getId());
        GoWebSocketServer userA = goUsers.get(blackId);
        if (userA != null) {
            userA.sendMessage(respGame.toJSONString());
        } else {
            throw new NullPointerException("null user not found");
        }
        GoWebSocketServer userB = goUsers.get(whiteId);
        if (userB != null) {
            userB.sendMessage(respGame.toJSONString());
        } else {
            throw new NullPointerException("null user not found");
        }
    }

    private void play(int x, int y) {
//        // 先判断自己是谁
//        if (goGame.getPlayer(1).getId().equals(this.user.getId())) {
//            this.game.setNextStepA(direction);
//        } else if (game.getPlayer(2).getId().equals(this.user.getId())) {
//            this.game.setNextStepB(direction);
//        }
    }
}
