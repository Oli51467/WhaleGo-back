package com.sdu.kob.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message extends BaseDomain {

    @Column(name = "send_user_id")
    private Long sendUserId;

    @Column(name = "receive_user_id")
    private Long receiveUserId;

    private String content;

    @Column(name = "send_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date sendTime;
}
