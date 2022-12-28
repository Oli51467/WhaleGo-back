package com.sdu.kob.entity.go;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Player {

    private Integer identifier;     // 1: 黑方 2:白方

    private Integer id;

    public Player(Integer identifier, Integer id) {
        this.identifier = identifier;
        this.id = id;
    }

    public Player(Integer identifier) {
        this.identifier = identifier;
    }
}
