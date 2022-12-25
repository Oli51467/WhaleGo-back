package com.sdu.kob.repository;

import com.sdu.kob.domain.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface BotDAO extends JpaRepository<Bot, Integer> {
    List<Bot> findByUserId(int userId);

    Bot findById(int botId);

    @Modifying
    @Transactional
    @Query(value = "update bot set user_id = ?2, title = ?3, description = ?4, content = ?5, rating = ?6, modify_time = ?7 where id = ?1", nativeQuery = true)
    void update(int id, int UserId, String title, String desc, String content, int rating, Date modifyTime);
}
