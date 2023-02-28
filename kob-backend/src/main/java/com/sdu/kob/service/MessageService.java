package com.sdu.kob.service;

import com.sdu.kob.response.ResponseResult;

public interface MessageService {
    ResponseResult getFriendsAndMessages(Long userId);

    ResponseResult sendMessages(Long userId, Long toId, String message);
}
