package com.sdu.kob.entity;

import com.sdu.kob.domain.User;
import lombok.Data;

@Data
public class Player {

    private Integer identifier;     // 1: 黑方 2:白方

    private Long id;

    private User user;

    public Player(Integer identifier, Long id, User user) {
        this.identifier = identifier;
        this.id = id;
        this.user = user;
    }
}
