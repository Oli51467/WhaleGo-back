package com.sdu.kob.controller;

import com.sdu.kob.domain.Post;
import com.sdu.kob.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PostController {
    @Autowired
    private PostService postService;

    @RequestMapping(value = "/api/post/add/", method = RequestMethod.POST)
    public Map<String, String> add(@RequestParam Map<String, String> data) {
        return postService.addPost(data);
    }

    @RequestMapping(value = "/api/post/getPosts/", method = RequestMethod.GET)
    public List<Post> getUserPosts(@RequestParam Map<String, String> data) {
        Integer userId = Integer.parseInt(data.get("user_id"));
        return postService.getAllPosts(userId);
    }

    @RequestMapping(value = "/api/post/remove/", method = RequestMethod.POST)
    public Map<String, String> removePost(@RequestParam Map<String, String> data) {
        return postService.removePost(data);
    }

    @RequestMapping(value = "/api/post/update/", method = RequestMethod.POST)
    public Map<String, String> updatePost(@RequestParam Map<String, String> data) {
        return postService.updatePost(data);
    }

    @RequestMapping(value = "/api/post/getAllPosts/", method = RequestMethod.GET)
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }
}
