package com.sdu.kob.consumer;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.Message;
import com.sdu.kob.domain.User;
import com.sdu.kob.engine.EngineRequestImpl;
import com.sdu.kob.entity.Room;
import com.sdu.kob.repository.FriendDAO;
import com.sdu.kob.repository.MessageDAO;
import com.sdu.kob.repository.RecordDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.concurrent.CopyOnWriteArraySet;

import static com.sdu.kob.entity.Board.BLACK;
import static com.sdu.kob.entity.Board.WHITE;

@Component
@ServerEndpoint("/go/websocket/{token}")
public class WebSocketServer {

    final public static ConcurrentHashMap<Long, WebSocketServer> users = new ConcurrentHashMap<>();
    final public static ConcurrentHashMap<Long, String> user2room = new ConcurrentHashMap<>();
    final public static ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    final public static CopyOnWriteArraySet<Long> matchingUsers = new CopyOnWriteArraySet<>();

    public static String addPlayerUrl;
    public static String removePlayerUrl;
    public static UserDAO userDAO;
    public static RecordDAO recordDAO;
    public static MessageDAO messageDAO;
    public static FriendDAO friendDAO;
    public static RestTemplate restTemplate;

    private User user;
    private Session session = null;

    @Autowired
    public void setUserMapper(UserDAO userDAO) {
        WebSocketServer.userDAO = userDAO;
    }

    @Autowired
    public void setRecordDAO(RecordDAO recordDAO) {
        WebSocketServer.recordDAO = recordDAO;
    }

    @Autowired
    public void setMessageDAO(MessageDAO messageDAO) {
        WebSocketServer.messageDAO = messageDAO;
    }

    @Autowired
    public void setFriendDAO(FriendDAO friendDAO) { WebSocketServer.friendDAO = friendDAO; }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }

    @Value("${url.matching.add}")
    private void setAddPlayerUrl(String addPlayerUrl) {
        WebSocketServer.addPlayerUrl = addPlayerUrl;
    }

    @Value("${url.matching.remove}")
    private void setRemovePlayerUrl(String removePlayerUrl) {
        WebSocketServer.removePlayerUrl = removePlayerUrl;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接 所有与连接相关的信息都会存到这个类中
        this.session = session;
        // 1. 从token中读取建立连接的用户是谁 拿到id
        long userId = JwtUtil.JWTAuthentication(token);
        // 2. 根据id查找用户
        this.user = userDAO.findById(userId);
        // 3. 将用户存下来
        if (this.user != null) {
            users.put(userId, this);
            System.out.println("Connected! " + user.getUserName());
        } else {
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        if (this.user != null) {
            Long userId = this.user.getId();
            users.remove(userId);
            System.out.println("Closed!");
        }
    }

    @OnMessage
    // 从Client接收消息 接收到前端信息时触发
    public void onMessage(String message, Session session) {    // 将message当作路由 根据请求作不同的处理
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("chat".equals(event)) {
            String msg = data.getString("msg");
            Long sendId = data.getLong("send_id");
            Long toId = data.getLong("to_id");
            Message messageRecord = new Message(sendId, toId, msg, new Date());
            messageDAO.save(messageRecord);
            friendDAO.increaseUnreadMessage(sendId, toId);
            int unread = friendDAO.findUnreadMessageCount(sendId, toId);
            int sumUnread = friendDAO.getMessageSum(toId);
            if (users.containsKey(toId)) {
                long cnt = messageDAO.count();
                WebSocketServer client = users.get(toId);
                JSONObject sendMsg = new JSONObject();
                sendMsg.put("content", msg);
                sendMsg.put("sendUserId", sendId);
                sendMsg.put("id", cnt);
                sendMsg.put("event", "chat");
                sendMsg.put("unread_cnt", unread);
                sendMsg.put("sum_unread", sumUnread);
                client.sendMessage(sendMsg.toJSONString());
            }
        } else if ("start".equals(event)) {
            startMatching();
        } else if ("cancel".equals(event)) {
            cancelMatching();
        } else if ("play".equals(event)) {
            Integer x = data.getInteger("x"), y = data.getInteger("y");
            Long userId = this.user.getId();
            if (x == -1 && y == -1) {
                if (Objects.equals(userId, rooms.get(user2room.get(userId)).blackPlayer.getId())) {
                    rooms.get(user2room.get(userId)).setLoser(BLACK);
                } else rooms.get(user2room.get(userId)).setLoser(WHITE);
            }
            rooms.get(user2room.get(userId)).setNextStep(x, y, false);
        } else if ("request_play".equals(event)) {
            Long friendId = data.getLong("friend_id");
            Long requestId = data.getLong("request_id");
            sendRequest2play(friendId, requestId);
        } else if ("request_draw".equals(event)) {
            String roomId = user2room.get(user.getId());
            Room playRoom = rooms.get(roomId);
            if (playRoom != null && playRoom.hasEngine) {
                playRoom.setLoser(-2);
                playRoom.setNextStep(-2, -2, false);
                return;
            }
            Long friendId = data.getLong("friend_id");
            sendRequest2DrawOrRegret(friendId, 1);
        } else if ("request_cancel".equals(event)) {
            Long friendId = data.getLong("friend_id");
            sendRequest2Cancel(friendId);
        } else if ("refuse_invitation".equals(event)) {
            Long friendId = data.getLong("friend_id");
            sendRefuseMessage(friendId);
        } else if ("accept_invitation".equals(event)) {
            Long aId = data.getLong("user_id");
            Long bId = data.getLong("friend_id");
            getReady(aId, bId);
            startGame(aId, bId);
        } else if ("accept_draw".equals(event)) {
            Long aId = data.getLong("user_id");
            rooms.get(user2room.get(aId)).setLoser(-2);
            rooms.get(user2room.get(aId)).setNextStep(-2, -2, false);
        } else if ("engine_play".equals(event)) {
            Long userId = data.getLong("user_id");
            Integer level = data.getInteger("level");
            startAIPlaying(userId, level);
        } else if ("request_regret".equals(event)) {
            String roomId = user2room.get(user.getId());
            Room playRoom = rooms.get(roomId);
            if (playRoom != null && playRoom.hasEngine) {
                playRoom.regretPlay(data.getInteger("which"));
                return;
            }
            Long friendId = data.getLong("friend_id");
            sendRequest2DrawOrRegret(friendId, 2);
        } else if ("accept_regret".equals(event)) {
            Long aId = data.getLong("user_id");
            Integer which = data.getInteger("which");
            rooms.get(user2room.get(aId)).regretPlay(which);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    private void getReady(Long aId, Long bId) {
        WebSocketServer aClient = users.get(aId);
        WebSocketServer bClient = users.get(bId);
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

    /**
     * 从后端向前端给一个用户发送信息
     */
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
     * 群发消息 维护心跳
     *
     * @param message 消息
     */
    public static void sendGroupMessage(String message) {
        for (Map.Entry<Long, WebSocketServer> entry : users.entrySet()) {
            entry.getValue().sendMessage(message);
        }
    }

    /**
     * 拒绝请求
     *
     * @param friendId 被拒绝的用户id
     */
    private void sendRefuseMessage(Long friendId) {
        if (users.get(friendId) == null) {
            matchingUsers.remove(this.user.getId());
            return;
        }
        WebSocketServer friendClient = users.get(friendId);
        matchingUsers.remove(friendId);
        matchingUsers.remove(this.user.getId());
        JSONObject resp = new JSONObject();
        resp.put("event", "friend_refuse");
        friendClient.sendMessage(resp.toJSONString());
    }

    // 取消发送请求
    private void sendRequest2Cancel(Long friendId) {
        if (users.get(friendId) == null) {
            matchingUsers.remove(this.user.getId());
            return;
        }
        WebSocketServer friendClient = users.get(friendId);
        matchingUsers.remove(friendId);
        matchingUsers.remove(this.user.getId());
        JSONObject resp = new JSONObject();
        resp.put("event", "request_cancel");
        friendClient.sendMessage(resp.toJSONString());
    }

    /**
     * 请求悔棋或和棋
     *
     * @param opponentId 对手的id
     * @param type       类型：1和棋 2悔棋
     */
    private void sendRequest2DrawOrRegret(Long opponentId, int type) {
        if (users.get(opponentId) == null) {
            return;
        }
        WebSocketServer friendClient = users.get(opponentId);
        JSONObject resp = new JSONObject();
        if (type == 1) resp.put("event", "request_draw");
        else if (type == 2) resp.put("event", "request_regret");
        friendClient.sendMessage(resp.toJSONString());
    }

    /**
     * 向另一名玩家发送对战请求
     *
     * @param friendId 被邀请人的id
     */
    private void sendRequest2play(Long friendId, Long requestId) {
        if (users.get(friendId) != null) {
            matchingUsers.add(requestId);
            matchingUsers.add(friendId);
            WebSocketServer friendClient = users.get(friendId);
            JSONObject resp = new JSONObject();
            JSONObject request_user = new JSONObject();
            User requestUser = userDAO.findById((long) requestId);
            resp.put("event", "request_play");
            request_user.put("id", requestUser.getId());
            request_user.put("username", requestUser.getUserName());
            request_user.put("avatar", requestUser.getAvatar());
            request_user.put("level", requestUser.getRating());
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
        matchData.add("rating", this.user.getRating());
        matchingUsers.add(this.user.getId());
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
        matchingUsers.remove(this.user.getId());
        restTemplate.postForObject(removePlayerUrl, cancelMatchData, String.class);
    }

    private void startAIPlaying(Long userId, Integer level) {
        JSONObject respGame = new JSONObject();
        User human = userDAO.findById((long) userId);
        User engine = new User(level);
        Room room = new Room(19, 19, userId, human, -1L, engine, true);
        user2room.put(userId, room.uuid);
        rooms.put(room.uuid, room);

        room.start();

        respGame.put("room_id", room.uuid);
        respGame.put("black_id", userId);
        respGame.put("white_id", -1);
        respGame.put("board", room.playBoard.board);

        // A回传B的信息
        JSONObject resp = new JSONObject();
        resp.put("event", "start");
        resp.put("opponent_username", "AI");
        resp.put("opponent_avatar", engine.getAvatar());
        resp.put("opponent_userid", -1);
        resp.put("game", respGame);
        // 用users哈希表获取A是哪个用户
        WebSocketServer user = users.get(userId);
        if (user != null) {
            user.sendMessage(resp.toJSONString());
        } else {
            throw new NullPointerException("null user not found");
        }
        EngineRequestImpl.initEngine(String.valueOf(userId));
    }

    // 开始下棋
    public static void startGame(Long aId, Long bId) {
        matchingUsers.remove(aId);
        matchingUsers.remove(bId);
        User a = userDAO.findById((long) aId);
        User b = userDAO.findById((long) bId);

        JSONObject respGame = new JSONObject();
        Random random = new Random();
        int seed = random.nextInt();
        // temp
        Long blackId, whiteId;
        Room room;
        if (seed * 10 >= 5) {
            blackId = aId;
            whiteId = bId;
            room = new Room(19, 19, blackId, a, whiteId, b, false);
        } else {
            blackId = bId;
            whiteId = aId;
            room = new Room(19, 19, blackId, b, whiteId, a, false);
        }

        // 将同步的地图同步给两名玩家
        if (users.get(a.getId()) != null) {
            user2room.put(a.getId(), room.uuid);
        }
        if (users.get(b.getId()) != null) {
            user2room.put(b.getId(), room.uuid);
        }
        rooms.put(room.uuid, room);

        room.start();

        respGame.put("black_id", blackId);
        respGame.put("white_id", whiteId);
        respGame.put("room_id", room.uuid);
        respGame.put("board", room.playBoard.board);

        // A回传B的信息
        JSONObject respA = new JSONObject();
        respA.put("event", "start");
        respA.put("opponent_username", b.getUserName());
        respA.put("opponent_avatar", b.getAvatar());
        respA.put("opponent_userid", b.getId());
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
        respB.put("opponent_userid", a.getId());
        respB.put("game", respGame);
        // 用users哈希表获取B是哪个用户
        WebSocketServer userB = users.get(b.getId());
        if (userB != null) {
            userB.sendMessage(respB.toJSONString());
        } else {
            throw new NullPointerException("null user not found");
        }
    }
}
