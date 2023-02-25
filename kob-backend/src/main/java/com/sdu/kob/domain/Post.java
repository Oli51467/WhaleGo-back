package com.sdu.kob.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@Data
public class Post extends BaseDomain {

    @Column(name = "user_id")
    private Long userId;

    @Transient
    private String username;

    @Transient
    private String userAvatar;

    private String title;

    private String content;

    @Transient
    private Integer stars;

    @Transient
    private String liked;

    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createTime;

    @Column(name = "modify_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date modifyTime;

    public Post(Long userId, String title, String content, Date createTime, Date modifyTime) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
    }
}
