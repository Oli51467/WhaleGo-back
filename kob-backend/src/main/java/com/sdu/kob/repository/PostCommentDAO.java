package com.sdu.kob.repository;

import com.sdu.kob.domain.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentDAO extends JpaRepository<PostComment, Long> {
}
