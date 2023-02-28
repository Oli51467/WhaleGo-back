package com.sdu.kob.repository;

import com.sdu.kob.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageDAO extends JpaRepository<Message, Long> {

    @Query(value = "select * from message where send_user_id = ?1 and receive_user_id = ?2 or send_user_id = ?2 and receive_user_id = ?1", nativeQuery = true)
    List<Message> getFriendsMessages(Long userId, Long friendId);
}
