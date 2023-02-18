package com.sdu.kob.consumer;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.User;
import com.sdu.kob.utils.JwtUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static com.sdu.kob.consumer.WebSocketServer.userDAO;

@Component
@ServerEndpoint("/websocket/{userid}")
public class WebSocketMessageServer {

    final public static ConcurrentHashMap<Integer, WebSocketMessageServer> users = new ConcurrentHashMap<>();

    private Session session = null;
    private User user;

    @OnOpen
    public void onOpen(Session session, @PathParam("userid") String userId) throws IOException {
        // 建立连接 所有与连接相关的信息都会存到这个类中
        this.session = session;
        // 1. 从token中读取建立连接的用户是谁 拿到id
//        int userId = JwtUtil.JWTAuthentication(token);
//        // 2. 根据id查找用户
        this.user = userDAO.findById(Integer.parseInt(userId));
        // 3. 将用户存下来
        if (this.user != null) {
            users.put(Integer.valueOf(userId), this);
            System.out.println("WsMessage Connected! " + user.getUserName());
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
            users.remove(userId);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject data = JSONObject.parseObject(message);
        String targetUsername = data.getString("target_username");
        User targetUser = userDAO.findByUserName(targetUsername);
        if (targetUser == null) {
            JSONObject res = new JSONObject();
            res.put("data", "用户不存在");
            this.sendMessage(res.toJSONString());
            return;
        }
        String msg = data.getString("msg");
        WebSocketMessageServer targetClient = users.get(targetUser.getId());
        if (targetClient == null) {
            JSONObject res = new JSONObject();
            res.put("data", "对方未连接");
            this.sendMessage(res.toJSONString());
            return;
        }
        JSONObject resp = new JSONObject();
        resp.put("data", "收到来自于" + user.getUserName() + "的信息：" + msg);
        targetClient.sendMessage(resp.toJSONString());
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
