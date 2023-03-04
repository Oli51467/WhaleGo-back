package com.sdu.kob.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostStar extends BaseDomain {
    private Long userId;

    private Long postId;

    private String isStar;
}
