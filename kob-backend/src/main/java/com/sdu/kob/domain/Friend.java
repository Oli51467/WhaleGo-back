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
public class Friend extends BaseDomain {

    @Column(name = "user_a")
    Long userA;

    @Column(name = "user_b")
    Long userB;

    String followed;

    Integer unreadMsgCount;
}
