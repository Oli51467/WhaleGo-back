package com.sdu.engine.service.impl;

import com.sdu.engine.service.EngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
public class EngineServiceImpl implements EngineService {

    private static RestTemplate restTemplate;
    private final static String receiveEnginePlayUrl = "http://127.0.0.1:3000/engine/receive/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        EngineServiceImpl.restTemplate = restTemplate;
    }

    @Override
    public String requestNextStep(Integer userId, String roomId) {
        System.out.println("requestNextStep" + userId + " " + roomId);
        Random random = new Random();
        int x = random.nextInt(19) % 19 + 1;
        int y = random.nextInt(19) % 19 + 1;

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", userId.toString());
        data.add("room_id", roomId);
        data.add("next_x", Integer.toString(x));
        data.add("next_y", Integer.toString(y));
        restTemplate.postForObject(receiveEnginePlayUrl, data, String.class);

        return "request success";
    }
}
