package com.sdu.kob.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Bot {

    private Integer id;

    private Integer userId;

    private String content;
}
