package com.sdu.kob.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Date;

@Entity
@NoArgsConstructor
@Data
public class PostComment extends BaseDomain {

    private Long userId;

    private Long postId;

    private Long parentCommentId;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date commentTime;

    @Transient
    private String username;

    @Transient
    private String userAvatar;

    @Transient
    private String presentCommentTime;

    public PostComment(Long userId, Long postId, Long parentCommentId, String content, Date commentTime) {
        this.userId = userId;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.commentTime = commentTime;
    }
}
