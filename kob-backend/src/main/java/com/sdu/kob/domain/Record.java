package com.sdu.kob.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record extends BaseDomain {

    @Column(name = "black_id")
    private Integer blackId;

    @Column(name = "white_id")
    private Integer whiteId;

    private String result;

    private String steps;

    private String level;
//
//    @ManyToOne
//    private User user;

    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
