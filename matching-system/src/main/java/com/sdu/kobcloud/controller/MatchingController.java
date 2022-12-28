package com.sdu.kobcloud.controller;

import com.sdu.kobcloud.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class MatchingController {

    @Autowired
    @Qualifier("MatchingService")
    MatchingService matchingService;

    @Autowired
    @Qualifier("GoMatchingService")
    MatchingService goMatchingService;

    // 每个关键字对应一个列表的value: MultiValueMap
    @RequestMapping(value = "/matching/add/", method = RequestMethod.POST)
    public String addPlayer(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        Integer rating = Integer.parseInt(Objects.requireNonNull(data.getFirst("rating")));
        return matchingService.addPlayer(userId, rating);
    }

    @RequestMapping(value = "/matching/remove/", method = RequestMethod.POST)
    public String removePlayer(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        return matchingService.removePlayer(userId);
    }

    @RequestMapping(value = "/go/matching/add/", method = RequestMethod.POST)
    public String addGoPlayer(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        Integer rating = Integer.parseInt(Objects.requireNonNull(data.getFirst("rating")));
        return goMatchingService.addPlayer(userId, rating);
    }

    @RequestMapping(value = "/go/matching/remove/", method = RequestMethod.POST)
    public String removeGoPlayer(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        return goMatchingService.removePlayer(userId);
    }
}
