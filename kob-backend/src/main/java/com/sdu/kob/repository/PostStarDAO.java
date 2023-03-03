package com.sdu.kob.repository;

import com.sdu.kob.domain.PostStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PostStarDAO extends JpaRepository<PostStar, Long> {

    PostStar findByUserIdAndPostId(Long userId, Long postId);

    @Modifying
    @Transactional
    @Query(value = "update post_star set is_star = ?3 where user_id = ?1 and post_id = ?2", nativeQuery = true)
    void update(long userId, long postId, String isStar);

    int countByIsStarAndPostId(String isStar, Long postId);
}
