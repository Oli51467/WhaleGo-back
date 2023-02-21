package com.sdu.kobcloud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Long userId;

    private Integer rating;

    private Integer waitingTime;    // 等待时间
}
