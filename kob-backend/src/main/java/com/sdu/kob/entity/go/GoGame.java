package com.sdu.kob.entity.go;

public class GoGame {

    private final Player blackPlayer;
    private final Player whitePlayer;
    private final Board board;

    public GoGame(Integer rows, Integer cols, Integer blackPlayerId, Integer whitePlayerId) {
        this.blackPlayer = new Player(1, blackPlayerId);
        this.whitePlayer = new Player(2, whitePlayerId);
        this.board = new Board(rows + 1, cols + 1, 0);
    }

    public Player getBlackPlayer(int identifier) {
        return identifier == 1 ? blackPlayer : whitePlayer;
    }
}
