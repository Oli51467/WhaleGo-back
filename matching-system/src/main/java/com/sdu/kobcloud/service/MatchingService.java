package com.sdu.kobcloud.service;

public interface MatchingService {
    String addPlayer(Integer userId, Integer rating);

    String removePlayer(Integer userId);
}
