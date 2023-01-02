package com.sdu.kob.consumer;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.entity.Bot;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    public static final String addPlayerUrl = "http://127.0.0.1:3001/matching/add/";
    public static final String removePlayerUrl = "http://127.0.0.1:3001/matching/remove/";
    private static final String content = "package com.sdu.bot.utils;\n" +
            "\n" +
            "import java.util.ArrayList;\n" +
            "import java.util.List;\n" +
            "\n" +
            "public class BotCodeTest implements com.sdu.bot.utils.BotInterface {\n" +
            "    public static int INT = 0x3f3f3f3f;\n" +
            "    public static int[][] path;\n" +
            "    public static int[][] g = new int[14][14];\n" +
            "    public static int pathLen = -1;\n" +
            "    public static boolean flag = true;\n" +
            "    public static int nextDirection = -1;\n" +
            "\n" +
            "\n" +
            "    static class Cell {\n" +
            "        public int x, y;\n" +
            "\n" +
            "        public Cell(int x, int y) {\n" +
            "            this.x = x;\n" +
            "            this.y = y;\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    private boolean check_tail_increasing(int step) {\n" +
            "        if (step <= 10) return true;\n" +
            "        return step % 3 == 1;\n" +
            "    }\n" +
            "\n" +
            "    public List<Cell> getCells(int sx, int sy, String steps) {\n" +
            "        steps = steps.substring(1, steps.length() - 1);\n" +
            "        List<Cell> res = new ArrayList<>();\n" +
            "\n" +
            "        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};\n" +
            "        int x = sx, y = sy;\n" +
            "        int step = 0;\n" +
            "        res.add(new Cell(x, y));\n" +
            "        for (int i = 0; i < steps.length(); i++) {\n" +
            "            int d = steps.charAt(i) - '0';\n" +
            "            x += dx[d];\n" +
            "            y += dy[d];\n" +
            "            res.add(new Cell(x, y));\n" +
            "            if (!check_tail_increasing(++step)) {\n" +
            "                res.remove(0);\n" +
            "            }\n" +
            "        }\n" +
            "        return res;\n" +
            "    }\n" +
            "\n" +
            "    public Integer nextMove(String input) {\n" +
            "        String[] strs = input.split(\"#\");\n" +
            "        for (int i = 0, k = 0; i < 13; i++) {\n" +
            "            for (int j = 0; j < 14; j++, k++) {\n" +
            "                if (strs[0].charAt(k) == '1') {//找到地图中所有的墙\n" +
            "                    g[i][j] = 1;//1：障碍物，0：空地\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);\n" +
            "        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);\n" +
            "\n" +
            "        List<Cell> aCells = getCells(aSx, aSy, strs[3]);\n" +
            "        List<Cell> bCells = getCells(bSx, bSy, strs[6]);\n" +
            "\n" +
            "        for (Cell c : aCells) g[c.x][c.y] = 2;//将地图中两条蛇身体的位置标记成障碍物\n" +
            "        for (Cell c : bCells) g[c.x][c.y] = 3;\n" +
            "\n" +
            "        //        a蛇头坐标\n" +
            "        int aHeadX = aCells.get(aCells.size() - 1).x;\n" +
            "        int aHeadY = aCells.get(aCells.size() - 1).y;\n" +
            "        //        b蛇头坐标\n" +
            "        int bHeadX = bCells.get(bCells.size() - 1).x;\n" +
            "        int bHeadY = bCells.get(bCells.size() - 1).y;\n" +
            "\n" +
            "        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};\n" +
            "        //顶点数\n" +
            "        int vertex = 13 * 14;\n" +
            "        //边数\n" +
            "        int edge = 0;\n" +
            "\n" +
            "        int[][] matrix = new int[vertex][vertex];\n" +
            "        //初始化邻接矩阵\n" +
            "        for (int i = 0; i < vertex; i++) {\n" +
            "            for (int j = 0; j < vertex; j++) {\n" +
            "                matrix[i][j] = INT;\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        //初始化路径数组\n" +
            "        path = new int[matrix.length][matrix.length];\n" +
            "\n" +
            "        //初始化边权值\n" +
            "\n" +
            "        for (int i = 0; i < 13; i++) {\n" +
            "            for (int j = 0; j < 14; j++) {\n" +
            "                if (g[i][j] == 1 || g[i][j] == 2) continue;\n" +
            "                //                右\n" +
            "                int dxx = 0, dyy = 1;\n" +
            "                int mx = i + dxx, my = j + dyy;\n" +
            "                if (my < 14 && (g[mx][my] == 0 || g[mx][my] == 3 || (mx == aHeadX && my == aHeadY))) {\n" +
            "                    matrix[i * 14 + j][mx * 14 + my] = 1;\n" +
            "                    matrix[mx * 14 + my][i * 14 + j] = 1;\n" +
            "                }\n" +
            "                //                下\n" +
            "                dxx = 1;\n" +
            "                dyy = 0;\n" +
            "                mx = i + dxx;\n" +
            "                my = j + dyy;\n" +
            "                if (mx < 13 && (g[mx][my] == 0 || g[mx][my] == 3 || (mx == aHeadX && my == aHeadY))) {\n" +
            "                    matrix[i * 14 + j][mx * 14 + my] = 1;\n" +
            "                    matrix[mx * 14 + my][i * 14 + j] = 1;\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "        for (int i = 0; i < 4; i++) {\n" +
            "            int mx = aHeadX + dx[i], my = aHeadY + dy[i];\n" +
            "            if (g[mx][my] == 0) {\n" +
            "                matrix[aHeadX * 14 + aHeadY][mx * 14 + my] = 1;\n" +
            "                matrix[mx * 14 + my][aHeadX * 14 + aHeadY] = 1;\n" +
            "            } else {\n" +
            "                matrix[aHeadX * 14 + aHeadY][mx * 14 + my] = INT;\n" +
            "                matrix[aHeadX * 14 + aHeadY][mx * 14 + my] = INT;\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "\n" +
            "        //调用算法计算最短路径\n" +
            "        floyd(matrix, aHeadX * 14 + aHeadY);\n" +
            "\n" +
            "        if (nextDirection != -1) return nextDirection;\n" +
            "\n" +
            "        for (int i = 0; i < 4; i++) {\n" +
            "            int x = aCells.get(aCells.size() - 1).x + dx[i];\n" +
            "            int y = aCells.get(aCells.size() - 1).y + dy[i];\n" +
            "            if (x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {\n" +
            "                return i;//选择一个合法的方向前进一格\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        return 0;\n" +
            "    }\n" +
            "\n" +
            "    // 非递归实现\n" +
            "    public static void floyd(int[][] matrix, Integer sources) {\n" +
            "        for (int i = 0; i < matrix.length; i++) {\n" +
            "            for (int j = 0; j < matrix.length; j++) {\n" +
            "                path[i][j] = -1;\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        for (int m = 0; m < matrix.length; m++) {\n" +
            "            for (int i = 0; i < matrix.length; i++) {\n" +
            "                for (int j = 0; j < matrix.length; j++) {\n" +
            "                    if (matrix[i][m] + matrix[m][j] < matrix[i][j]) {\n" +
            "                        matrix[i][j] = matrix[i][m] + matrix[m][j];\n" +
            "                        //记录经由哪个点到达\n" +
            "                        path[i][j] = m;\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        int minLength = INT, position = -1;\n" +
            "        for (int i = 0; i < matrix.length; i++) {\n" +
            "            for (int j = 0; j < matrix.length; j++) {\n" +
            "                if (i != j && i == sources && g[j / 14][j % 14] == 3) {\n" +
            "                    if (matrix[i][j] != INT) {\n" +
            "                        findPath(i, j);\n" +
            "\n" +
            "                        if (matrix[i][j] < minLength) {\n" +
            "                            minLength = matrix[i][j];\n" +
            "                            position = pathLen;\n" +
            "                        }\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "        if (minLength != INT) {\n" +
            "            int headX = sources / 14, headY = sources % 14;\n" +
            "            int nextX = position / 14, nextY = position % 14;\n" +
            "            int dx = nextX - headX, dy = nextY - headY;\n" +
            "            if (dx == -1 && dy == 0) {\n" +
            "                nextDirection = 0;\n" +
            "            } else if (dx == 0 && dy == 1) {\n" +
            "                nextDirection = 1;\n" +
            "            } else if (dx == 1 && dy == 0) {\n" +
            "                nextDirection = 2;\n" +
            "            } else if (dx == 0 && dy == -1) {\n" +
            "                nextDirection = 3;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    //递归寻找路径\n" +
            "    public static void findPath(int i, int j) {\n" +
            "        int m = path[i][j];\n" +
            "        if (m == -1) {\n" +
            "            return;\n" +
            "        }\n" +
            "\n" +
            "        findPath(i, m);\n" +
            "        if (flag) {\n" +
            "            pathLen = m;\n" +
            "            flag = false;\n" +
            "        }\n" +
            "\n" +
            "        findPath(m, j);\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "\n";

    // 存储所有user对应的连接 当匹配成功后，将匹配成功的连接返回给用户 加static为的是users对所有实例均可见
    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();

    private User user;
    private Session session = null; // 用户信息存储到session中

    private static UserDAO userDAO; // 用静态变量的set函数注入
    public static SnakeRecordDAO snakeRecordDAO;
    public Game game = null;

    public static RestTemplate restTemplate;   // 两个spring间通信的工具

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

    public static User putBot2Pool(Integer id) {
        UUID uuid = UUID.randomUUID();
        User user = new User("bot", uuid.toString().substring(0, 4), 1500, "https://cdn.acwing.com/media/article/image/2022/07/07/1_535cd642fd-kob2.png", 0, 0);
        user.setId(id);
        return user;
    }

    // *** 由匹配系统匹配出的结果 匹配出来两名玩家进行对战 ***
    public static void startGame(Integer aId, Integer bId) {
        System.out.println(aId + " " + bId);
        User a = userDAO.findById((int) aId);
        User b = userDAO.findById((int) bId);
        Bot botA = null, botB = null;
        if (a == null) {
            a = putBot2Pool(aId);
            botA = new Bot(aId, aId, content);
            botA.setId(aId);
        }
        if (b == null) {
            b = putBot2Pool(bId);
            botB = new Bot(bId, bId, content);
            botB.setId(bId);
        }


        Game game = new Game(13, 14, 20, a.getId(), botA, b.getId(), botB);
        game.createMap();
        // 将同步的地图同步给两名玩家
        if (users.get(a.getId()) != null) {
            users.get(a.getId()).game = game;
        }
        if (users.get(b.getId()) != null) {
            users.get(b.getId()).game = game;
        }

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
        if (aId > 0 && null != userA) {
            userA.sendMessage(respA.toJSONString());
        }

        // B回传A的信息
        JSONObject respB = new JSONObject();
        respB.put("event", "start");
        respB.put("opponent_username", a.getUserName());
        respB.put("opponent_avatar", a.getAvatar());
        respB.put("game", respGame);
        // 用users哈希表获取B是哪个用户
        WebSocketServer userB = users.get(b.getId());
        if (bId > 0 && null != userB) {
            userB.sendMessage(respB.toJSONString());
        }
    }

    // mode=0:匹配 mode=1:人机
    private void startMatching(Integer mode) {
        System.out.println("start Matching");
        // 向matching Server发出一个请求开始匹配
        MultiValueMap<String, String> matchData = new LinkedMultiValueMap<>();
        matchData.add("user_id", this.user.getId().toString());
        matchData.add("rating", this.user.getRating().toString());
        restTemplate.postForObject(addPlayerUrl, matchData, String.class);
        // 添加一个bot
        if (mode == 1) {
            int id;
            for (id = -1; id >= -100; id--) {
                if (users.get(id) == null) {
                    users.put(id, this);
                    break;
                }
            }
            MultiValueMap<String, String> botData = new LinkedMultiValueMap<>();
            botData.add("user_id", String.valueOf(id));
            botData.add("rating", this.user.getRating().toString());
            restTemplate.postForObject(addPlayerUrl, botData, String.class);
        }
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
            if (game.getPlayer(1).getBotId() == 0)
                this.game.setNextStepA(direction);
        } else if (game.getPlayer(2).getId().equals(this.user.getId())) {
            if (game.getPlayer(2).getBotId() == 0)
                this.game.setNextStepB(direction);
        }
    }

    @OnMessage
    // 从Client接收消息 接收到前端信息时触发
    public void onMessage(String message, Session session) {    // 将message当作路由 根据请求作不同的处理
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start".equals(event)) {
            System.out.println(data.getInteger("mode"));
            startMatching(data.getInteger("mode"));
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
