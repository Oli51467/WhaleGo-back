package com.sdu.kob.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    /**
     * 成功返回的状态码
     */
    SUCCESS(200, "success"),

    ROOM_NOT_EXIST(10001, "房间不存在"),

    USERNAME_EXIST(10002, "用户名已存在");

    private int code;
    private String msg;
}
