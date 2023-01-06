package com.sdu.kob.entity.go;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 棋盘
public class Board {
    public final static int BLACK_STONE = 1;
    public final static int WHITE_STONE = 2;

    private final int width;
    private final int height;
    public int playCount;
    public final Point[][] points;
    public List<Point> recordPoints;
    public Set<Point> capturedStones;
    private final int initialHandicap;
    public final GameRecord gameRecord;

    private Player P1, P2, actualPlayer;
    private int handicap;

    public Board(int width, int height, int handicap) {
        this.width = width;
        this.height = height;
        this.initialHandicap = handicap;
        this.points = new Point[width + 1][height + 1];
        this.gameRecord = new GameRecord(width, height);
        this.recordPoints = new ArrayList<>();
        initBoard();
    }

    private void initBoard() {
        // 初始化对局双方
        P1 = new Player(1);
        P2 = new Player(2);
        actualPlayer = P1;

        // 初始化棋盘
        for (int x = 1; x <= this.width; x++) {
            for (int y = 1; y <= this.height; y++) {
                points[x][y] = new Point(this, x, y);
            }
        }
        handicap = 0;
        playCount = 0;
    }

    public boolean isInBoard(int x, int y) {
        return (x > 0 && x < width && y > 0 && y < height);
    }

    public boolean isInBoard(Point Point) {
        int x = Point.getX();
        int y = Point.getY();
        return isInBoard(x, y);
    }

    public Point getPoint(int x, int y) {
        if (isInBoard(x, y)) {
            return points[x][y];
        } else {
            return null;
        }
    }

    public boolean play(Point point, Player player, boolean handleKo) {
        GameTurn currentTurn = null;
        boolean ko = false;

        // 棋子应该在棋盘内
        if (!isInBoard(point)) return false;

        // 棋子不能重叠
        if (point.getGroup() != null) return false;

        // 为判断打劫 要记录吃掉的棋子和吃掉的组
        capturedStones = null;
        Set<Group> capturedGroups = null;
        if (handleKo) {
            capturedStones = new HashSet<>();
            capturedGroups = new HashSet<>();
        }


        Set<Group> adjGroups = point.getAdjacentGroups();
        Group newGroup = new Group(point, player);
        point.setGroup(newGroup);
        for (Group group : adjGroups) {
            if (group.getOwner() == player) {
                newGroup.add(group, point);
            } else {
                group.removeLiberty(point);
                if (group.getLiberties().size() == 0) {
                    if (handleKo) {
                        capturedStones.addAll(group.getStones());
                        capturedGroups.add(new Group(group));
                    }
                    group.die();
                }
            }
        }

        if (handleKo) {
            currentTurn = gameRecord.getLastTurn().toNext(point.getX(), point.getY(), player.getIdentifier(), capturedStones);
            for (GameTurn turn : gameRecord.getTurns()) {
                if (turn.equals(currentTurn)) {
                    ko = true;
                    break;
                }
            }
            // 判断打劫
            if (ko) {
                for (Group chain : capturedGroups) {
                    for (Point stone : chain.getStones()) {
                        stone.setGroup(chain);
                    }
                }
            }
        }

        // 不能自杀
        if (newGroup.getLiberties().size() == 0 || ko) {
            for (Group chain : point.getAdjacentGroups()) {
                chain.getLiberties().add(point);
            }
            point.setGroup(null);
            return false;
        }
        // 落子有效
        for (Point stone : newGroup.getStones()) {
            stone.setGroup(newGroup);
        }
        if (handleKo) {
            gameRecord.apply(currentTurn);
        }
        recordPoints.add(point);
        playCount ++;
        return true;
    }

    public boolean play(int x, int y, Player player) {
        Point point = getPoint(x, y);
        if (point == null) {
            return false;
        }
        return play(point, player, true);
    }

    public Player getPlayer() {
        return actualPlayer;
    }

    public void nextPlayer() {
        changePlayer(false);
    }

    public void precedentPlayer() {
        changePlayer(true);
    }

    public void changePlayer(boolean undo) {
        if (handicap < initialHandicap && !undo) {
            handicap++;
        } else if (undo && this.gameRecord.nbrPreceding() < initialHandicap) {
            handicap--;
        } else {
            if (actualPlayer == P1) {
                actualPlayer = P2;
            } else {
                actualPlayer = P1;
            }
        }
    }

    public boolean undo() {
        if (gameRecord.hasPreceding()) {
            gameRecord.undo();
            GameTurn last = gameRecord.getLastTurn();
            takeGameTurn(last, P1, P2);
            precedentPlayer();
            return true;
        } else {
            return false;
        }
    }

    public void redo() {
        if (gameRecord.hasFollowing()) {
            gameRecord.redo();
            GameTurn next = gameRecord.getLastTurn();
            takeGameTurn(next, P1, P2);
            nextPlayer();
        }
    }

    public void takeGameTurn(GameTurn gameTurn, Player one, Player two) {
        this.freeIntersections();
        int[][] boardState = gameTurn.getBoardState();
        for (int x = 1; x <= width; x++) {
            for (int y = 1; y <= height; y++) {
                int state = boardState[x][y];
                if (state == BLACK_STONE) {
                    play(getPoint(x, y), one, false);
                } else if (state == WHITE_STONE) {
                    play(getPoint(x, y), two, false);
                }
            }
        }
    }

    public void freeIntersections() {
        for (int i = 1; i < width; i++) {
            for (int j = 1; j < height; j++) {
                Point point = getPoint(i, j);
                point.setGroup(null);
            }
        }
    }

    public String getSteps2Sgf() {
        StringBuilder game = new StringBuilder();
        String which = "B";
        for (GameTurn gameTurn : this.gameRecord.preceding) {
            int x = gameTurn.x, y = gameTurn.y;
            if (!isInBoard(x, y)) continue;
            game.append(which).append('[').append(x).append(',').append(y).append(']');
            which = (which.equals("B") ? "W" : "B");
        }
        return game.toString();
    }
}

