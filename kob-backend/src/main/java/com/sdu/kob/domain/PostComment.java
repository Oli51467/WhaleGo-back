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

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    private String content;

    @Column(name = "comment_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date commentTime;

    @Transient
    private String username;

    @Transient
    private String userAvatar;

    public PostComment(Long userId, Long postId, Long parentCommentId, String content, Date commentTime) {
        this.userId = userId;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.commentTime = commentTime;
    }
}
