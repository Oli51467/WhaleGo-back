package com.sdu.kob.service.impl;

import com.sdu.kob.domain.Friend;
import com.sdu.kob.domain.Post;
import com.sdu.kob.domain.StarPost;
import com.sdu.kob.repository.PostDAO;
import com.sdu.kob.repository.StarPostDAO;
import com.sdu.kob.service.StarPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("StarPostService")
public class StarPostServiceImpl implements StarPostService {

    @Autowired
    private StarPostDAO starPostDAO;

    @Autowired
    private PostDAO postDAO;

    @Override
    public String starPost(Integer userId, Integer postId) {
        Post post = postDAO.findById((int)postId);
        if (null == post) {
            return "该帖子不存在";
        }
        StarPost starPost = starPostDAO.findByUserIdAndPostId(userId, postId);
        if (starPost == null) {
            StarPost star = new StarPost(userId, postId, "true");
            starPostDAO.save(star);
        } else {
            starPostDAO.update(userId, postId, "true");
        }
        return "success";
    }

    @Override
    public String unstarPost(Integer userId, Integer postId) {
        Post post = postDAO.findById((int)postId);
        if (null == post) {
            return "该帖子不存在";
        }
        StarPost starPost = starPostDAO.findByUserIdAndPostId(userId, postId);
        if (starPost == null) {
            StarPost star = new StarPost(userId, postId, "false");
            starPostDAO.save(star);
        } else {
            starPostDAO.update(userId, postId, "false");
        }
        return "success";
    }
}
