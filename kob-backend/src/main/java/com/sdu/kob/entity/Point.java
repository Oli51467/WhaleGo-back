package com.sdu.kob.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 描述可以放置棋子位置的类 与棋盘关联
public class Point {

    private final Board board;
    private final int x;
    private final int y;

    // 描述该点是否在一个组内或属于哪个组
    private Group group;

    public Point(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        this.group = null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Group getGroup() {
        return group;
    }

    // Group可以为空
    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean isEmpty() {
        return group == null;
    }

    // 拿到相邻棋子所属的组
    public Set<Group> getAdjacentGroups() {
        Set<Group> adjacentGroups = new HashSet<>();
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        for (int i = 0; i < dx.length; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (board.isInBoard(newX, newY)) {
                Point adjPoint = board.getPoint(newX, newY);
                if (adjPoint.group != null) {
                    adjacentGroups.add(adjPoint.group);
                }
            }
        }
        return adjacentGroups;
    }

    public List<Point> getEmptyNeighbors() {
        List<Point> emptyNeighbors = new ArrayList<>();
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        for (int i = 0; i < dx.length; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            if (board.isInBoard(newX, newY)) {
                Point adjPoint = board.getPoint(newX, newY);
                if (adjPoint.isEmpty()) {
                    emptyNeighbors.add(adjPoint);
                }
            }
        }
        return emptyNeighbors;
    }
}
