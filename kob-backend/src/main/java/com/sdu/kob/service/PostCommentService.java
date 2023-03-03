package com.sdu.kob.service;

import com.sdu.kob.response.ResponseResult;

public interface PostCommentService {
    ResponseResult postComment(Long userId, Long postId, Long parentCommentId, String content);

    ResponseResult getPostComments(Long postId);
}
