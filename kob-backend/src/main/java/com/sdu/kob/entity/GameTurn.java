package com.sdu.kob.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameTurn {
    public int[][] boardState;
    public final int x, y;
    private final int hashCode;
    public Set<Point> capturedStones;

    public GameTurn(int width, int height) {
        boardState = new int[width + 1][height + 1];
        for (int i = 1; i <= 19; i ++ ) {
            for (int j = 1; j <= 19; j ++ ) {
                boardState[i][j] = 0;
            }
        }
        x = -1;
        y = -1;
        hashCode = Arrays.deepHashCode(boardState);
        this.capturedStones = new HashSet<>();
    }

    private GameTurn(GameTurn prev, int x, int y, int playerId, Set<Point> freedPoint) {
        int width = prev.boardState.length;
        int height = prev.boardState[0].length;

        boardState = new int[width][height];
        for (int i = 1; i < width; i++) {
            boardState[i] = prev.boardState[i].clone();
        }
        this.x = x;
        this.y = y;

        if (x > 0 && y > 0) {
            boardState[x][y] = playerId;
        }

        for (Point point : freedPoint) {
            boardState[point.getX()][point.getY()] = 0;
        }
        hashCode = Arrays.deepHashCode(boardState);
        this.capturedStones = freedPoint;
    }

    public GameTurn toNext(int x, int y, int playerId, Set<Point> freedPoint) {
        return new GameTurn(this, x, y, playerId, freedPoint);
    }

    public int[][] getBoardState() {
        return boardState;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        GameTurn castedObj = (GameTurn) obj;

        return hashCode == castedObj.hashCode && Arrays.deepEquals(this.boardState, castedObj.boardState);
    }
}
