package com.sdu.kob.service;

import com.sdu.kob.domain.Post;

import java.util.List;
import java.util.Map;

public interface PostService {
    List<Post> getAllPosts(Integer userId);

    Map<String, String> addPost(Map<String, String> data);

    Map<String, String> removePost(Map<String, String> data);

    Map<String, String> updatePost(Map<String, String> data);
}
