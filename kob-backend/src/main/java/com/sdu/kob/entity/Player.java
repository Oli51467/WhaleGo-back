package com.sdu.kob.entity;

import com.sdu.kob.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Player {

    private Integer identifier;     // 1: 黑方 2:白方

    private Integer id;

    private User user;

    public Player(Integer identifier, Integer id, User user) {
        this.identifier = identifier;
        this.id = id;
        this.user = user;
    }

    public Player(Integer identifier) {
        this.identifier = identifier;
    }
}
