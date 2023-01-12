package com.sdu.kob.repository;

import com.sdu.kob.domain.Friend;
import com.sdu.kob.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface PostDAO extends JpaRepository<Post, Integer> {
    List<Post> findByUserId(Integer userId);

    Post findById(int id);

    @Modifying
    @Transactional
    @Query(value = "update post set user_id = ?2, title = ?3, content = ?4, modify_time = ?5 where id = ?1", nativeQuery = true)
    void updatePost(int id, int UserId, String title, String content, Date modifyTime);
}
