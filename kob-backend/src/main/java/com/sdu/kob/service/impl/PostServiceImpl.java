package com.sdu.kob.service.impl;

import com.sdu.kob.domain.Post;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.PostDAO;
import com.sdu.kob.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("PostService")
public class PostServiceImpl implements PostService {

    @Autowired
    PostDAO postDAO;

    @Override
    public List<Post> getAllPosts(Integer userId) {
        return postDAO.findByUserId(userId);
    }

    @Override
    public Map<String, String> addPost(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String title = data.get("title");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();

        if (title == null || title.length() == 0) {
            map.put("msg", "标题不能为空");
            return map;
        }

        if (title.length() > 15) {
            map.put("msg", "标题长度不能大于15");
            return map;
        }

        if (content == null || content.length() == 0) {
            map.put("msg", "内容不能为空");
            return map;
        }

        if (content.length() > 10000) {
            map.put("msg", "内容长度不能超过10000");
            return map;
        }
        Date now = new Date();
        Post post = new Post(user.getId(), user.getUserName(), user.getAvatar(), title, content, now, now);

        postDAO.save(post);
        map.put("msg", "success");

        return map;
    }

    @Override
    public Map<String, String> removePost(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int postId = Integer.parseInt(data.get("post_id"));
        Post bot = postDAO.findById(postId);
        Map<String, String> map = new HashMap<>();

        if (bot == null) {
            map.put("msg", "帖子不存在或已被删除");
            return map;
        }

        if (!bot.getUserId().equals(user.getId())) {
            map.put("msg", "没有权限删除该帖子");
            return map;
        }

        postDAO.deleteById(postId);

        map.put("msg", "success");
        return map;
    }

    @Override
    public Map<String, String> updatePost(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int postId = Integer.parseInt(data.get("post_id"));

        String title = data.get("title");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();

        if (title == null || title.length() == 0) {
            map.put("msg", "标题不能为空");
            return map;
        }

        if (title.length() > 15) {
            map.put("msg", "标题长度不能大于15");
            return map;
        }

        if (content == null || content.length() == 0) {
            map.put("msg", "内容不能为空");
            return map;
        }

        if (content.length() > 10000) {
            map.put("msg", "内容长度不能超过10000");
            return map;
        }

        Post post = postDAO.findById(postId);

        if (post == null) {
            map.put("msg", "帖子不存在或已被删除");
            return map;
        }

        if (!post.getUserId().equals(user.getId())) {
            map.put("msg", "没有权限修改该帖子");
            return map;
        }

        postDAO.updatePost(postId, user.getId(), title, content, new Date());

        map.put("msg", "success");

        return map;
    }

    @Override
    public List<Post> getAllPosts() {
        return postDAO.findAll();
    }
}
