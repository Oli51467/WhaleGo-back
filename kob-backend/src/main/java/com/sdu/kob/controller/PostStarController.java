package com.sdu.kob.controller;

import com.sdu.kob.service.PostStarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PostStarController {

    @Autowired
    private PostStarService postStarService;

    @RequestMapping(value = "/api/post/star/", method = RequestMethod.POST)
    public String starPost(@RequestParam Map<String, String> data) {
        Long userId = Long.valueOf(data.get("user_id"));
        Long postId = Long.valueOf(data.get("post_id"));
        return postStarService.starPost(userId, postId);
    }

    @RequestMapping(value = "/api/post/unstar/", method = RequestMethod.POST)
    public String unstarPost(@RequestParam Map<String, String> data) {
        Long userId = Long.valueOf(data.get("user_id"));
        Long postId = Long.valueOf(data.get("post_id"));
        return postStarService.unstarPost(userId, postId);
    }
}
