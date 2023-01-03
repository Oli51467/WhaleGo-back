package com.sdu.kob.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;

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

    private Integer win;

    private Integer lose;

    private Integer state;
}
