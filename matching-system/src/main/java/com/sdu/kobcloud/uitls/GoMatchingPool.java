package com.sdu.kobcloud.uitls;

import com.sdu.kobcloud.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class GoMatchingPool extends Thread {

    // 存储所有用户 多线程公用 需要加锁
    private static List<Player> players = new ArrayList<>();

    private final ReentrantLock lock = new ReentrantLock();

    private static RestTemplate restTemplate;
    private static final String startGoGameCallbackUrl = "http://127.0.0.1:3000/go/match/startGame/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        GoMatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, String rating) {
        lock.lock();
        try {
            players.add(new Player(userId, RatingUtil.getRating(rating), 0));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            List<Player> dummyPlayers = new ArrayList<>();
            for (Player player: players) {
                if (!player.getUserId().equals(userId)) {
                    dummyPlayers.add(player);
                }
            }
            players = dummyPlayers;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 将所有玩家等待时间+1
     */
    private void increaseWaitingTime() {
        for (Player player : players) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    /**
     * 判断两名玩家是否可以匹配
     * 如果分差<=min(Wait_Time_a, Wait_Time_b)是考虑双方需求原则，<= max则是满足一方原则
     * @return 是否匹配
     */
    private boolean checkMatch(Player a, Player b) {
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDelta <= waitingTime * 10;
    }

    /**
     * 给玩家a、b返回匹配结果
     * @param a 玩家a
     * @param b 玩家b
     */
    private void sendMatchResult(Player a, Player b) {
        MultiValueMap<String, String> callbackData = new LinkedMultiValueMap<>();
        callbackData.add("a_id", a.getUserId().toString());
        callbackData.add("b_id", b.getUserId().toString());
        System.out.println("ready return");
        restTemplate.postForObject(startGoGameCallbackUrl, callbackData, String.class);
    }

    /**
     * 尝试匹配所有玩家
     */
    private void matchPlayers() {
        boolean[] used = new boolean[players.size()];
        for (int i = 0; i < players.size(); i ++ ) {
            if (used[i]) continue;
            for (int j = i + 1; j < players.size(); j ++ ) {
                if (used[j]) continue;
                Player a = players.get(i), b = players.get(j);
                if (checkMatch(a, b)) {
                    used[i] = used[j] = true;
                    sendMatchResult(a, b);
                    break;
                }
            }
        }

        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i ++ ) {
            if (!used[i]) {
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increaseWaitingTime();
                    matchPlayers();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
