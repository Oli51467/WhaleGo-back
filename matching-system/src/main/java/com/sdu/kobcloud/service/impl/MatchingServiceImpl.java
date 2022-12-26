package com.sdu.kobcloud.service.impl;

import com.sdu.kobcloud.service.MatchingService;
import com.sdu.kobcloud.uitls.MatchingPool;
import org.springframework.stereotype.Service;

@Service("MatchingService")
public class MatchingServiceImpl implements MatchingService {

    public final static MatchingPool matchingPool = new MatchingPool();

    @Override
    public String addPlayer(Integer userId, Integer rating) {
        System.out.println("add player:" + userId + " " + rating);
        matchingPool.addPlayer(userId, rating);
        return "add player success";
    }

    @Override
    public String removePlayer(Integer userId) {
        System.out.println("remove player" + userId);
        matchingPool.removePlayer(userId);
        return "remove player success";
    }
}
