package com.sdu.kob.service.impl;

import com.sdu.kob.domain.Post;
import com.sdu.kob.domain.PostStar;
import com.sdu.kob.repository.PostDAO;
import com.sdu.kob.repository.PostStarDAO;
import com.sdu.kob.service.PostStarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("StarPostService")
public class PostStarServiceImpl implements PostStarService {

    @Autowired
    private PostStarDAO postStarDAO;

    @Autowired
    private PostDAO postDAO;

    @Override
    public String starPost(Long userId, Long postId) {
        Post post = postDAO.findById((long)postId);
        if (null == post) {
            return "该帖子不存在";
        }
        PostStar postStar = postStarDAO.findByUserIdAndPostId(userId, postId);
        if (postStar == null) {
            PostStar star = new PostStar(userId, postId, "true");
            postStarDAO.save(star);
        } else {
            postStarDAO.update(userId, postId, "true");
        }
        return "success";
    }

    @Override
    public String unstarPost(Long userId, Long postId) {
        Post post = postDAO.findById((long)postId);
        if (null == post) {
            return "该帖子不存在";
        }
        PostStar postStar = postStarDAO.findByUserIdAndPostId(userId, postId);
        if (postStar == null) {
            PostStar star = new PostStar(userId, postId, "false");
            postStarDAO.save(star);
        } else {
            postStarDAO.update(userId, postId, "false");
        }
        return "success";
    }
}
