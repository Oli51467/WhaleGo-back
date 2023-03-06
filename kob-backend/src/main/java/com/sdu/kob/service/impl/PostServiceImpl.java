package com.sdu.kob.service.impl;

import com.sdu.kob.domain.Post;
import com.sdu.kob.domain.PostStar;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.PostCommentDAO;
import com.sdu.kob.repository.PostDAO;
import com.sdu.kob.repository.PostStarDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("PostService")
public class PostServiceImpl implements PostService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PostStarDAO postStarDAO;

    @Autowired
    private PostCommentDAO postCommentDAO;

    @Override
    public List<Post> getAllPosts(Long userId) {
        List<Post> posts = postDAO.findByUserId(userId);
        return filterPost(posts);
    }

    @Override
    public List<Post> getAllPosts() {
        List<Post> posts = postDAO.findAll();
        return filterPost(posts);
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
        Post post = new Post(user.getId(), title, content, now, now);

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

        long postId = Long.parseLong(data.get("post_id"));
        Post post = postDAO.findById(postId);
        Map<String, String> map = new HashMap<>();

        if (post == null) {
            map.put("msg", "帖子不存在或已被删除");
            return map;
        }

        if (!post.getUserId().equals(user.getId())) {
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

        long postId = Long.parseLong(data.get("post_id"));

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

    List<Post> filterPost(List<Post> posts) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        Long curUserId = loginUser.getUser().getId();
        // 取出post里面的userid
        List<Long> userIds = posts.stream().map(Post::getUserId).collect(Collectors.toList());
        List<User> users = userDAO.findAllById(userIds);
        HashMap<Long, User> id2user = new HashMap<>();
        for (User user : users) {
            id2user.put(user.getId(), user);
        }
        for (Post post : posts) {
            Long postId = post.getId();
            post.setUsername(id2user.get(post.getUserId()).getUserName());
            post.setUserAvatar(id2user.get(post.getUserId()).getAvatar());
            Integer stars = postStarDAO.countByIsStarAndPostId("true", postId);
            Integer commentsCount = postCommentDAO.countByPostId(postId);
            post.setStars(stars);
            post.setCommentsCount(commentsCount);
            PostStar postStar = postStarDAO.findByUserIdAndPostId(curUserId, postId);
            if (null == postStar || postStar.getIsStar().equals("false")) {
                post.setLiked("false");
            }
            else if (postStar.getIsStar().equals("true")) {
                post.setLiked("true");
            }
        }
        return posts;
    }
}
