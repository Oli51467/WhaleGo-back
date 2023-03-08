package com.sdu.kob.engine;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EngineRequestImpl {

    private static String requestEngineUrl;
    private static String initEngineUrl;
    private static String resignEngineUrl;
    private static String ownerEngineUrl;
    private static String winRateEngineUrl;
    private static RestTemplate restTemplate;

    @Value("${url.engine.request}")
    private void setRequestEngineUrl(String requestEngineUrl) {
        EngineRequestImpl.requestEngineUrl = requestEngineUrl;
    }

    @Value("${url.engine.init}")
    private void setInitEngineUrl(String initEngineUrl) {
        EngineRequestImpl.initEngineUrl = initEngineUrl;
    }

    @Value("${url.engine.resign}")
    private void setResignEngineUrl(String resignEngineUrl) {
        EngineRequestImpl.resignEngineUrl = resignEngineUrl;
    }

    @Value("${url.engine.territory}")
    private void setOwnerEngineUrl(String ownerEngineUrl) {
        EngineRequestImpl.ownerEngineUrl = ownerEngineUrl;
    }

    @Value("${url.engine.win-rate}")
    private void setWinRateEngineUrl(String winRateEngineUrl) { EngineRequestImpl.winRateEngineUrl = winRateEngineUrl; }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        EngineRequestImpl.restTemplate = restTemplate;
    }

    public static void initEngine(String userId) {
        JSONObject request = new JSONObject();
        request.put("user_id", String.valueOf(userId));
        request.put("rules", "");
        request.put("play", "1");
        request.put("komi", "");
        request.put("level", "p");
        request.put("boardsize", "19");
        request.put("initialStones", "[]");
        restTemplate.postForObject(initEngineUrl, request, JSONObject.class);
    }

    public static JSONObject requestNextStep(String userId, String board, int currentPlayer) {
        JSONObject data = new JSONObject();
        data.put("user_id", userId);
        data.put("board", board);
        data.put("current_player", currentPlayer);
        return restTemplate.postForObject(requestEngineUrl, data, JSONObject.class);
    }

    public static void requestTerritory(String state) {
        JSONObject data = new JSONObject();
        data.put("initialStones", state);
        data.put("level", "p");
        JSONObject o = restTemplate.postForObject(ownerEngineUrl, data, JSONObject.class);
        System.out.println(o);
    }

    public static void resign(String userId) {
        JSONObject data = new JSONObject();
        data.put("user_id", userId);
        restTemplate.postForObject(resignEngineUrl, data, JSONObject.class);
    }

    public static String getWinRate(String moves) {
        StringBuilder res = new StringBuilder();
        JSONObject data = new JSONObject();
        data.put("user_id", "999");
        data.put("moves", moves);
        data.put("level", "p");
        JSONObject resp = restTemplate.postForObject(winRateEngineUrl, data, JSONObject.class);
        if (null != resp && resp.getInteger("code") == 1000) {
            JSONArray respData = resp.getJSONArray("data");
            for (int i = 0; i < respData.size(); i ++ ) {
                JSONObject stepState = respData.getJSONObject(i);
                System.out.println("stepState: " + stepState);
                String winRate = stepState.getString("winrate");
                System.out.println("winRate: " + winRate);
                res.append(winRate).append(",");
            }
            System.out.println(respData);
        }
        return res.toString();
    }
}
