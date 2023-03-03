package com.sdu.kob.service;

public interface PostStarService {
    String starPost(Long userId, Long postId);

    String unstarPost(Long userId, Long postId);
}
