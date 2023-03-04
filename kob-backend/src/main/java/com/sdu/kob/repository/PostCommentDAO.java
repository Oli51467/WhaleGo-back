package com.sdu.kob.repository;

import com.sdu.kob.domain.PostComment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentDAO extends JpaRepository<PostComment, Long> {

    List<PostComment> findByPostId(Long postId, Sort sort);
}
