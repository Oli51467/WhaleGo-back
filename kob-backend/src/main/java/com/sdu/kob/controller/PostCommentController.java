package com.sdu.kob.controller;

import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.PostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PostCommentController {

    @Autowired
    private PostCommentService postCommentService;

    @RequestMapping(value = "/api/post/comment/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult postComment(@RequestParam Map<String, String> data) {
        Long userId = Long.parseLong(data.get("user_id"));
        Long postId = Long.parseLong(data.get("post_id"));
        Long parentCommentId = Long.parseLong(data.get("parent_comment_id"));
        String content = data.get("content");
        return postCommentService.postComment(userId, postId, parentCommentId, content);
    }

    @RequestMapping(value = "/api/post/comment/get/", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getPostComments(@RequestParam Map<String, String> data) {
        Long postId = Long.parseLong(data.get("post_id"));
        return postCommentService.getPostComments(postId);
    }
}
