package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.PostComment;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.PostCommentDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.response.ResponseCode;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.PostCommentService;
import com.sdu.kob.utils.DateUtil;
import net.bytebuddy.TypeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("CommentPostService")
public class PostCommentServiceImpl implements PostCommentService {

    @Autowired
    private PostCommentDAO postCommentDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    public ResponseResult postComment(Long userId, Long postId, Long parentCommentId, String content) {
        PostComment comment = new PostComment(userId, postId, parentCommentId, content, new Date());
        postCommentDAO.save(comment);
        return new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), null);
    }

    @Override
    public ResponseResult getPostComments(Long postId) {
        int commentsCount = 0;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<PostComment> comments = postCommentDAO.findByPostId(postId, sort);
        for (int i = comments.size() - 1; i >= 0; i -- ) {
            PostComment comment = comments.get(i);
            User commentUser = userDAO.findById((long)comment.getUserId());
            comment.setUsername(commentUser.getUserName());
            comment.setUserAvatar(commentUser.getAvatar());
            comment.setPresentCommentTime(DateUtil.transformDatetime(comment.getCommentTime()));
            commentsCount ++;
        }
        JSONObject resp = new JSONObject();
        resp.put("comments_count", commentsCount);
        resp.put("comments", comments);
        return new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), resp);
    }
}
