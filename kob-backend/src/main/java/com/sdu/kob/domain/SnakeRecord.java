package com.sdu.kob.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SnakeRecord extends BaseDomain {

    @Column(name = "a_id")
    private Integer aId;

    @Column(name = "a_sx")
    private Integer aSx;

    @Column(name = "a_sy")
    private Integer aSy;

    @Column(name = "b_id")
    private Integer bId;

    @Column(name = "b_sx")
    private Integer bSx;

    @Column(name = "b_sy")
    private Integer bSy;

    @Column(name = "a_steps")
    private String aSteps;

    @Column(name = "b_steps")
    private String bSteps;

    private String map;

    private String loser;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @CreatedDate
    @Column(name = "create_time")
    private Date createTime;
}
