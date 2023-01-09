package com.sdu.kob.consumer;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.User;
import com.sdu.kob.entity.Room;
import com.sdu.kob.entity.go.GameTurn;
import com.sdu.kob.entity.go.GoGame;
import com.sdu.kob.repository.RecordDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.utils.JwtUtil;
import com.sdu.kob.utils.RatingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/go/websocket/{token}")
public class WebSocketServer {

    public static final String addPlayerUrl = "http://127.0.0.1:3001/go/matching/add/";
    public static final String removePlayerUrl = "http://127.0.0.1:3001/go/matching/remove/";

    final public static ConcurrentHashMap<Integer, WebSocketServer> goUsers = new ConcurrentHashMap<>();
    final public static ConcurrentHashMap<Integer, String> user2room = new ConcurrentHashMap<>();
    final public static ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    private User user;
    private Session session = null;

    private static UserDAO userDAO;
    public static RecordDAO recordDAO;
    private static RestTemplate restTemplate;

    @Autowired
    public void setUserMapper(UserDAO userDAO) {
        WebSocketServer.userDAO = userDAO;
    }

    @Autowired
    public void setRecordDAO(RecordDAO recordDAO) {
        WebSocketServer.recordDAO = recordDAO;
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
        // 3. 将用户存下来
        if (this.user != null) {
            goUsers.put(userId, this);
            System.out.println("Connected! " + user.getUserName());
        } else {
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        // 关闭连接
        System.out.println("Closed!");
        if (this.user != null) {
            Integer userId = this.user.getId();
            goUsers.remove(userId);
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
        } else if ("play".equals(event)) {
            Integer x = data.getInteger("x"), y = data.getInteger("y");
            Integer userId = this.user.getId();
            if (x == -1 && y == -1) {
                int loser;
                if (Objects.equals(userId, rooms.get(user2room.get(userId)).getGoGame().blackPlayer.getId())) loser = 1;
                else loser = 2;
                rooms.get(user2room.get(userId)).getGoGame().setLoser(loser);
            }
            rooms.get(user2room.get(userId)).getGoGame().setNextStep(x, y);
        } else if ("request_play".equals(event)) {
            Integer friendId = data.getInteger("friend_id");
            Integer requestId = data.getInteger("request_id");
            sendRequest2play(friendId, requestId);
        } else if ("request_draw".equals(event)) {
            Integer friendId = data.getInteger("friend_id");
            sendRequest2Draw(friendId);
        } else if ("refuse_draw".equals(event)) {
            Integer friendId = data.getInteger("friend_id");
            sendRefuseMessage2Draw(friendId);
        } else if ("request_cancel".equals(event)) {
            Integer friendId = data.getInteger("friend_id");
            sendRequest2Cancel(friendId);
        } else if ("refuse_invitation".equals(event)) {
            Integer friendId = data.getInteger("friend_id");
            sendRefuseMessage(friendId);
        } else if ("accept_invitation".equals(event)) {
            Integer aId = data.getInteger("user_id");
            Integer bId = data.getInteger("friend_id");
            getReady(aId, bId);
            startGame(aId, bId);
        } else if ("accept_draw".equals(event)){
            Integer aId = data.getInteger("user_id");
            rooms.get(user2room.get(aId)).getGoGame().setLoser(-2);
            rooms.get(user2room.get(aId)).getGoGame().setNextStep(-2, -2);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    private void getReady(Integer aId, Integer bId) {
        WebSocketServer aClient = goUsers.get(aId);
        WebSocketServer bClient = goUsers.get(bId);
        JSONObject resp = new JSONObject();
        resp.put("event", "ready");
        aClient.sendMessage(resp.toJSONString());
        bClient.sendMessage(resp.toJSONString());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    // 拒绝和棋
    private void sendRefuseMessage2Draw(Integer friendId) {
        WebSocketServer friendClient = goUsers.get(friendId);
        JSONObject resp = new JSONObject();
        resp.put("event", "refuse_draw");
        friendClient.sendMessage(resp.toJSONString());
    }

    // 拒绝请求
    private void sendRefuseMessage(Integer friendId) {
        WebSocketServer friendClient = goUsers.get(friendId);
        JSONObject resp = new JSONObject();
        resp.put("event", "friend_refuse");
        friendClient.sendMessage(resp.toJSONString());
    }

    // 取消发送请求
    private void sendRequest2Cancel(Integer friendId) {
        WebSocketServer friendClient = goUsers.get(friendId);
        JSONObject resp = new JSONObject();
        resp.put("event", "request_cancel");
        friendClient.sendMessage(resp.toJSONString());
    }

    // 请求和棋
    private void sendRequest2Draw(Integer opponentId) {
        WebSocketServer friendClient = goUsers.get(opponentId);
        JSONObject resp = new JSONObject();
        resp.put("event", "request_draw");
        friendClient.sendMessage(resp.toJSONString());
    }

    /**
     * 向另一名玩家发送对战请求
     * @param friendId 被邀请人的id
     */
    private void sendRequest2play(Integer friendId, Integer requestId) {
        if (goUsers.get(friendId) != null) {
            WebSocketServer friendClient = goUsers.get(friendId);
            JSONObject resp = new JSONObject();
            JSONObject request_user = new JSONObject();
            User requestUser = userDAO.findById((int)requestId);
            resp.put("event", "request_play");
            request_user.put("id", requestUser.getId());
            request_user.put("username", requestUser.getUserName());
            request_user.put("avatar", requestUser.getAvatar());
            request_user.put("level", RatingUtil.getRating2Level(requestUser.getRating()));
            request_user.put("win", requestUser.getWin());
            request_user.put("lose", requestUser.getLose());
            resp.put("request_user", request_user);
            friendClient.sendMessage(resp.toJSONString());
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

    // 开始下棋
    public static void startGame(Integer aId, Integer bId) {
        User a = userDAO.findById((int) aId);
        User b = userDAO.findById((int) bId);

        JSONObject respGame = new JSONObject();
        Random random = new Random();
        int seed = random.nextInt();
        // temp
        Integer blackId, whiteId;
        GoGame goGame = null;
        if (seed * 10 >= 5) {
            blackId = aId;
            whiteId = bId;
            goGame = new GoGame(19, 19, blackId, a, whiteId, b);
        } else {
            blackId = bId;
            whiteId = aId;
            goGame = new GoGame(19, 19, blackId, b, whiteId, a);
        }

        // 将同步的地图同步给两名玩家
        if (goUsers.get(a.getId()) != null) {
            user2room.put(a.getId(), goGame.uuid);
        }
        if (goUsers.get(b.getId()) != null) {
            user2room.put(b.getId(), goGame.uuid);
        }
        // 将用户加入到房间
        Set<Integer> usersInRoom = new HashSet<>();
        usersInRoom.add(a.getId());
        usersInRoom.add(b.getId());
        rooms.put(goGame.uuid, new Room(goGame.uuid, usersInRoom, blackId, whiteId, "", goGame));

        goGame.start();

        GameTurn lastTurn = goGame.board.gameRecord.getLastTurn();
        respGame.put("black_id", blackId);
        respGame.put("white_id", whiteId);
        respGame.put("room_id", goGame.uuid);
        respGame.put("board", lastTurn.boardState);

        // A回传B的信息
        JSONObject respA = new JSONObject();
        respA.put("event", "start");
        respA.put("opponent_username", b.getUserName());
        respA.put("opponent_avatar", b.getAvatar());
        respA.put("opponent_userid", b.getId());
        respA.put("game", respGame);
        // 用users哈希表获取A是哪个用户
        WebSocketServer userA = goUsers.get(a.getId());
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
        respB.put("opponent_userid", a.getId());
        respB.put("game", respGame);
        // 用users哈希表获取B是哪个用户
        WebSocketServer userB = goUsers.get(b.getId());
        if (userB != null) {
            userB.sendMessage(respB.toJSONString());
        } else {
            throw new NullPointerException("null user not found");
        }
    }
}
