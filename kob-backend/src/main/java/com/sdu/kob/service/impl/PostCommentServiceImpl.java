package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.PostComment;
import com.sdu.kob.repository.PostCommentDAO;
import com.sdu.kob.response.ResponseCode;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.PostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("CommentPostService")
public class PostCommentServiceImpl implements PostCommentService {

    @Autowired
    private PostCommentDAO postCommentDAO;

    @Override
    public ResponseResult postComment(Long userId, Long postId, Long parentCommentId, String content) {
        PostComment comment = new PostComment(userId, postId, parentCommentId, content, new Date());
        postCommentDAO.save(comment);
        return new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), null);
    }

    @Override
    public ResponseResult getPostComments(Long postId) {
        List<PostComment> comments = postCommentDAO.findByPostId(postId);
        long commentsCount = postCommentDAO.count();
        JSONObject resp = new JSONObject();
        resp.put("comments_count", commentsCount);
        resp.put("comments", comments);
        return new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), resp);
    }


}
