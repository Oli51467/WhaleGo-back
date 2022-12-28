package com.sdu.kob.entity.go;

import java.util.HashSet;
import java.util.Set;

public class Group {
    private final Set<Point> stones;
    private final Set<Point> liberties;
    private final Player owner;

    public Group(Set<Point> stones, Set<Point> liberties, Player owner) {
        this.stones = stones;
        this.liberties = liberties;
        this.owner = owner;
    }

    public Group(Point point, Player owner) {
        stones = new HashSet<>();
        stones.add(point);
        this.owner = owner;
        liberties = new HashSet<>(point.getEmptyNeighbors());
    }

    public Group(Group group) {
        this.stones = new HashSet<>(group.stones);
        this.liberties = new HashSet<>(group.liberties);
        this.owner = group.owner;
    }

    public Player getOwner() {
        return owner;
    }

    public Set<Point> getLiberties() {
        return liberties;
    }

    public Set<Point> getStones() {
        return stones;
    }

    public void add(Group group, Point playedStone) {
        this.stones.addAll(group.stones);
        this.liberties.addAll(group.liberties);
        this.liberties.remove(playedStone);
    }

    public void removeLiberty(Point playedStone) {
        Group newGroup = new Group(stones, liberties, owner);
        newGroup.liberties.remove(playedStone);
    }

    public void die() {
        for (Point rollingStone : this.stones) {
            rollingStone.setGroup(null);
            Set<Group> adjacentGroups = rollingStone.getAdjacentGroups();
            for (Group group : adjacentGroups) {
                group.liberties.add(rollingStone);
            }
        }
    }
}
