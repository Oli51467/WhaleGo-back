package com.sdu.kobcloud.service.impl;

import com.sdu.kobcloud.service.MatchingService;
import com.sdu.kobcloud.uitls.GoMatchingPool;
import org.springframework.stereotype.Service;

@Service("GoMatchingService")
public class GoMatchingServiceImpl implements MatchingService {

    public final static GoMatchingPool goMatchingPool = new GoMatchingPool();

    @Override
    public String addPlayer(Integer userId, String rating) {
        System.out.println("add player:" + userId + " " + rating);
        goMatchingPool.addPlayer(userId, rating);
        return "add player success";
    }

    @Override
    public String removePlayer(Integer userId) {
        System.out.println("remove player" + userId);
        goMatchingPool.removePlayer(userId);
        return "remove player success";
    }
}
