package com.sdu.kob.engine;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EngineRequest {

    private static String requestEngineUrl;
    private static String initEngineUrl;
    private static String resignEngineUrl;
    private static RestTemplate restTemplate;

    @Value("${url.engine.request}")
    private void setRequestEngineUrl(String requestEngineUrl) {
        EngineRequest.requestEngineUrl = requestEngineUrl;
    }

    @Value("${url.engine.init}")
    private void setInitEngineUrl(String initEngineUrl) {
        EngineRequest.initEngineUrl = initEngineUrl;
    }

    @Value("${url.engine.resign}")
    private void setResignEngineUrl(String resignEngineUrl) {
        EngineRequest.resignEngineUrl = resignEngineUrl;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        EngineRequest.restTemplate = restTemplate;
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

    public static void resign(String userId) {
        JSONObject data = new JSONObject();
        data.put("user_id", userId);
        restTemplate.postForObject(resignEngineUrl, data, JSONObject.class);
    }
}
