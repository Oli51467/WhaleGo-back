package com.sdu.kob.engine;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.utils.HttpClientUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

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

    @Value("${url.engine.winrate}")
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

    public static String getWinRate(String userid, String moves) {
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("user_id", userid));
        nameValuePairs.add(new BasicNameValuePair("moves", moves));
        String resp = HttpClientUtil.get(winRateEngineUrl, nameValuePairs);
        if (resp == null) return "";
        JSONObject getResp = JSONObject.parseObject(resp);
        String v = getResp.getString("key");
        System.out.println(resp + " " + v);
        return v;
    }
}
