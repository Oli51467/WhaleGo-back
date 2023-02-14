package com.sdu.kobcloud.service;

public interface MatchingService {
    String addPlayer(Integer userId, String rating);

    String removePlayer(Integer userId);
}
