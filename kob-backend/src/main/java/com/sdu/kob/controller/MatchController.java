package com.sdu.kob.controller;

import com.sdu.kob.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class MatchController {

    @Autowired
    @Qualifier("MatchService")
    private MatchService matchService;

    @Autowired
    @Qualifier("MatchGoService")
    private MatchService goMatchService;

    @RequestMapping(value = "/match/startGame/", method = RequestMethod.POST)
    public String startGame(@RequestParam MultiValueMap<String, String> data) {
        Integer aId = Integer.parseInt(Objects.requireNonNull(data.getFirst("a_id")));
        Integer bId = Integer.parseInt(Objects.requireNonNull(data.getFirst("b_id")));
        return matchService.startGame(aId, bId);
    }

    @RequestMapping(value = "/go/match/startGame/", method = RequestMethod.POST)
    public String startGoGame(@RequestParam MultiValueMap<String, String> data) {
        Integer aId = Integer.parseInt(Objects.requireNonNull(data.getFirst("a_id")));
        Integer bId = Integer.parseInt(Objects.requireNonNull(data.getFirst("b_id")));
        return goMatchService.startGame(aId, bId);
    }
}
