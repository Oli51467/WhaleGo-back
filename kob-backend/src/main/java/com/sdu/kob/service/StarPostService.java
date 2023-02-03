package com.sdu.kob.service;

public interface StarPostService {
    String starPost(Integer userId, Integer postId);

    String unstarPost(Integer userId, Integer postId);
}
