package com.sdu.kob.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

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

    public User(Integer level) {
        this.userName = "AI" + level;
        this.password = "123";
        this.profile = "";
        this.rating = "3段";
        this.avatar = "https://cdn.acwing.com/media/user/profile/photo/221601_md_b93784dc2c.jpg";
        this.win = 0;
        this.lose = 0;
        this.guestsCount = 0;
        this.guests = "";
        this.recentRecords = "";
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.avatar = "https://cdn.acwing.com/media/user/profile/photo/73457_lg_28f38d989d.jpeg";
        this.profile = "";
        this.win = 0;
        this.lose = 0;
        this.rating = "";
        this.recentRecords = "";
        this.guestsCount = 0;
        this.guests = "";
    }
}
