package com.sdu.kob.service;

import com.sdu.kob.response.ResponseResult;

public interface MessageService {
    ResponseResult getFriendsAndMessages(Long userId);

    ResponseResult getMessageCount(Long userId);

    ResponseResult getPeer2PeerUnreadMessageCount(Long sendId, Long receiveId);

    ResponseResult clearUnreadMessageCount(Long sendId, Long receiveId);
}
