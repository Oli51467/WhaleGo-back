package com.sdu.kob.entity.go;

import com.sdu.kob.consumer.WebSocketServer;

import static com.sdu.kob.consumer.WebSocketServer.users;

public class GoGame {

    private final Player blackPlayer;
    private final Player whitePlayer;
    public final Board board;

    public GoGame(Integer rows, Integer cols, Integer blackPlayerId, Integer whitePlayerId) {
        this.blackPlayer = new Player(1, blackPlayerId);
        this.whitePlayer = new Player(2, whitePlayerId);
        this.board = new Board(rows + 1, cols + 1, 0);
    }

    public Player getPlayer(int identifier) {
        return identifier == 1 ? blackPlayer : whitePlayer;
    }

    private void sendAllMessage(String message) {
        WebSocketServer clientA = users.get(blackPlayer.getId());
        if (clientA != null) {
            clientA.sendMessage(message);
        }
        WebSocketServer clientB = users.get(whitePlayer.getId());
        if (clientB != null) {
            clientB.sendMessage(message);
        }
    }
}
