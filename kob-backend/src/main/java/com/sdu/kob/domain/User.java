package com.sdu.kob.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

import static com.sdu.kob.common.SystemConstants.WEB_IP;
import static com.sdu.kob.consumer.WebSocketServer.matchingUsers;
import static com.sdu.kob.consumer.WebSocketServer.user2room;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User extends BaseDomain {

    @Column(name = "username")
    private String userName;

    private String password;

    private String phone;

    private String profile;

    private String rating;

    private String avatar;

    private Integer win;

    private Integer lose;

    @Column(name = "guests_count")
    private Integer guestsCount;

    @Column(name = "recent_guests")
    private String guests;

    @Column(name = "recent_records")
    private String recentRecords;

    public String getStatus() {
        if (matchingUsers.contains(this.getId())) {
            return "matching";
        } else if (user2room.containsKey(this.getId())) {
            return "playing";
        } else {
            return "stand";
        }
    }

    public String getAvatar() {
        return WEB_IP + avatar;
    }

    public User(Integer level) {
        this.userName = "AI" + level;
        this.password = "123";
        this.phone = "";
        this.profile = "";
        this.rating = "3段";
        this.avatar = "images/level_1.png";
        this.win = 0;
        this.lose = 0;
        this.guestsCount = 0;
        this.guests = "";
        this.recentRecords = "";
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.phone = "";
        this.avatar = "images/level_1.png";
        this.profile = "";
        this.win = 0;
        this.lose = 0;
        this.rating = "3段";
        this.recentRecords = "";
        this.guestsCount = 0;
        this.guests = "";
    }
}
