package com.sdu.kob.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RedisUtil {

    private static StringRedisTemplate stringRedisTemplate;

    @Autowired
    private void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        RedisUtil.stringRedisTemplate = stringRedisTemplate;
    }

    public static void addUserUnreadMessageCount(String toId) {
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(toId + "-msg_count"))) {
            stringRedisTemplate.opsForValue().set(toId + "-msg_count", "1");
        } else {
            int count = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(toId + "-msg_count")));
            count ++;
            stringRedisTemplate.opsForValue().set(toId + "-msg_count", Integer.toString(count));
        }
    }

    public static void addPeer2PeerUnreadMessages(String sendUser, String receiveUser) {
        if (Boolean.FALSE.equals(stringRedisTemplate.opsForHash().hasKey(sendUser, receiveUser))) {
            stringRedisTemplate.opsForHash().put(sendUser, receiveUser, 1);
        } else {
            int count = Integer.parseInt(String.valueOf(stringRedisTemplate.opsForHash().get(sendUser, receiveUser)));
            count ++;
            stringRedisTemplate.opsForHash().put(sendUser, receiveUser, count);
        }
    }

    public static int getUserMessageCount(String userId) {
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(userId + "-msg_count"))) {
            return 0;
        } else {
            return Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(userId + "-msg_count")));
        }
    }

    public static int getPeer2PeerUnreadMessage(String sendUser, String receiveUser) {
        if (Boolean.FALSE.equals(stringRedisTemplate.opsForHash().hasKey(sendUser, receiveUser))) {
            return 0;
        } else {
            return Integer.parseInt(String.valueOf(stringRedisTemplate.opsForHash().get(sendUser, receiveUser)));
        }
    }
}
