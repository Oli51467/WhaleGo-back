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
    @Qualifier("GoMatchingService")
    MatchingService goMatchingService;


    @RequestMapping(value = "/go/matching/add/", method = RequestMethod.POST)
    public String addGoPlayer(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        String rating = Objects.requireNonNull(data.getFirst("rating"));
        return goMatchingService.addPlayer(userId, rating);
    }

    @RequestMapping(value = "/go/matching/remove/", method = RequestMethod.POST)
    public String removeGoPlayer(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        return goMatchingService.removePlayer(userId);
    }
}
