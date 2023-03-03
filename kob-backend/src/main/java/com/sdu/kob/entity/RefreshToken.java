package com.sdu.kob.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class RefreshToken {

    private Long userId;

    private String token;

    private Instant expiryDate;
}
