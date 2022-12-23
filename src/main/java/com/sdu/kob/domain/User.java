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
public class User extends BaseDomain {

    @Column(name = "username")
    private String userName;

    private String password;

    private Integer rating;

    private String avatar;
}
