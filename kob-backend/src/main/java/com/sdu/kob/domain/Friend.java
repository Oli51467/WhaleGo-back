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
    Integer userA;

    @Column(name = "user_b")
    Integer userB;

    String followed;
}
