package com.sdu.kobcloud.service;

public interface MatchingService {
    String addPlayer(Long userId, String rating);

    String removePlayer(Long userId);
}
