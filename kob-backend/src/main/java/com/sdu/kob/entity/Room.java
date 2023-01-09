package com.sdu.kob.entity;

import com.sdu.kob.entity.go.GoGame;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    String id;

    Set<Integer> users;

    Integer blackPlayer;

    Integer whitePlayer;

    String state;

    GoGame goGame;


}
